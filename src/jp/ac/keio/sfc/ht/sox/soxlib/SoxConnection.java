package jp.ac.keio.sfc.ht.sox.soxlib;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.keio.sfc.ht.sox.protocol.Device;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.disco.packet.DiscoverItems.Item;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.Matcher;
import org.simpleframework.xml.transform.Transform;

public class SoxConnection {

	private XMPPTCPConnection con;
	private String jid;
	private String password;
	private String server;
	private String service;
	private HashMap<String, PubSubManager> pubsubManagers;
	private boolean isDebugEnable;

	/*
	 * Constructor of SoxConnection class.
	 * server and service is usually same like sox.ht.sfc.keio.ac.jp.
	 * jid is without server name. Not "guest@sox.ht.sfc.keio.ac.jp", just use "guest".
	 */
	public SoxConnection(String _server, String _service, String _jid,
			String _password, boolean _isDebugEnable) throws SmackException,
			IOException, XMPPException {

		this.server = _server;
		this.service = _service;
		this.jid = _jid;
		this.password = _password;
		this.isDebugEnable = _isDebugEnable;

		this.connect();
	}
	
	/*
	 * Anonymous login
	 */
	public SoxConnection(String _server, boolean _isDebugEnable)
			throws SmackException, IOException, XMPPException {
		this(_server, _server, null, null, _isDebugEnable);
	}
	
	/*
	 * server and service is usually same. this is easy way.
	 */
	public SoxConnection(String _server, String _jid, String _pass,
			boolean _isDebugEnable) throws SmackException, IOException,
			XMPPException {

		this(_server, _server, _jid, _pass, _isDebugEnable);
	}


	
	
	/**
	 * Connect to SOX server. This is automatically called in constructor.
	 * 
	 * @throws SmackException
	 * @throws IOException
	 * @throws XMPPException
	 */

	public void connect() throws SmackException, IOException, XMPPException {

		SmackConfiguration.setDefaultPacketReplyTimeout(300 * 1000);

		XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration
				.builder().setHost(server).setPort(5222)
				.setServiceName(service).setSecurityMode(SecurityMode.disabled)
				.setDebuggerEnabled(isDebugEnable).setConnectTimeout(30 * 1000)
				.build();

		con = new XMPPTCPConnection(config);

		con.connect();

		if (jid != null || password != null) {
			// login with jid and password
			con.login(jid, password);
		} else {
			// anonymous login
			con.loginAnonymously();
		}

		
		/**
		 * add PubSubManager in HashMap for SOX federation You can
		 * discover/subscribe nodes in different SOX server by using
		 * PubSubManager which relates to different SOX server In this case, put
		 * PubSubManager of connected SOX server to HashMap
		 */
		pubsubManagers = new HashMap<String, PubSubManager>();
		PubSubManager manager = new PubSubManager(con, "pubsub." + service);
		pubsubManagers.put(service, manager);
	}

	/*
	 * Disconnect to the server
	 */
	public void disconnect() {
		if (con != null && con.isConnected()) {
			con.disconnect();
		}
	}

	
	/*
	 * Create user account
	 */
	public void createUser(String _user, String _pass, String _email){
		AccountManager accountManager = AccountManager.getInstance(con);
		try{
			
			Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("username", _user.replace( "@sox-dev.ht.sfc.keio.ac.jp", "" ));
            attributes.put("password", _pass);
            attributes.put("email", _email);

			accountManager.createAccount(_user, _pass, attributes);
            
            
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Basic Functionalities: node create, delete, discover
	 */

	/*
	 * Create Node
	 */
	public void createNode(String nodeName, Device device, AccessModel aModel,
			PublishModel pModel) throws NoResponseException,
			XMPPErrorException, NotConnectedException {

		// Checking this connection is not anonymous login. If not, proceed.
		if (jid != null) {

			PubSubManager manager = pubsubManagers.get(service);

			/*
			 * Meta Node Configuration
			 */
			ConfigureForm metaform = new ConfigureForm(DataForm.Type.submit);
			metaform.setAccessModel(aModel);
			metaform.setPublishModel(pModel);
			metaform.setPersistentItems(true); // meta data should be saved in
												// SOX as persistent item.
			metaform.setMaxItems(1); // meta data should be saved in SOX.
	
			// create _meta node
			LeafNode eventNode_meta = (LeafNode)manager.createNode(nodeName + "_meta",metaform);

			
			// Set the configuration to meta node
			//eventNode_meta.sendConfigurationForm(metaform);

			/*
			 * Let's publish meta data to meta node.
			 */
			StringWriter writer = new StringWriter(); // To transform data
														// object into XML
														// string
			Persister serializer = new Persister(new Matcher() {
				public Transform match(Class type) throws Exception {
					if (type.isEnum()) {
						return new SoxEnumTransform(type);
					}
					return null;
				}
			});

			try {
				serializer.write(device, writer);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Creating publish item
			SimplePayload payload = new SimplePayload(nodeName,
					"http://jabber.org/protocol/sox", writer.toString());

			PayloadItem<SimplePayload> pi = new PayloadItem<SimplePayload>(
					null, payload);

			// Publish meta data
			try {
				eventNode_meta.publish(pi);
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			/*
			 * Then create _data node.
			 */
			

			/**
			 * Set configuration of the node. This is very important because it
			 * affects ejabberd behaviors such as DB access
			 */
			/*
			 * Data Node Configuration
			 */
			ConfigureForm dataform = new ConfigureForm(DataForm.Type.submit);
			dataform.setAccessModel(aModel);
			dataform.setPublishModel(pModel);
			dataform.setMaxItems(1); // Sensor data should not saved in SOX.
			dataform.setPersistentItems(true); // Also should not saved in SOX
												// as persistent Items.
			dataform.setMaxPayloadSize(300000); // This is current limitation. we
												// cannot set over 60000. So,
												// current max size of each data
												// is about 60KB. I don't know
												// how to set more than 60KB..

			// create _data node
			LeafNode eventNode_data = (LeafNode)manager.createNode(nodeName + "_data",dataform);

			// Set the configuration to data node
			//eventNode_data.sendConfigurationForm(dataform);



		} else {
			System.out
					.println("error: please use non anonymous user for node creation");
			System.exit(1);
		}

	}

	/*
	 * Delete sensor node. For example, deleteNode(exampleNode) means to delete both exampleNode_meta and exampleNode_data.
	 */
	public void deleteNode(String nodeName) throws NoResponseException,
			XMPPErrorException, NotConnectedException {

		// checking this is non anonymous login
		if (jid != null) {
			PubSubManager manager = pubsubManagers.get(service);
			manager.deleteNode(nodeName + "_meta");
			manager.deleteNode(nodeName + "_data");
		} else {
			System.out
					.println("error: please use non anonymous user for node dalete");
			System.exit(1);
		}
	}
	
	/*
	 * This is for testing. atomicNodeName have to be specified like "exampleNode_meta", not "exampleNode".
	 * Usually, to delete sensor node, you should use deleteNode method.
	 */
	public void deleteXMPPPubSubNode(String atomicNodeName)
			throws NoResponseException, XMPPErrorException,
			NotConnectedException {
		PubSubManager manager = pubsubManagers.get(service);
		manager.deleteNode(atomicNodeName);

	}
	

	/*
	 * Discover sensor list from target sox server This method reply virtual
	 * node name (like exampleNode, not exampleNode_meta or exampleNode_data).
	 * If you want to actual XMPP PubSub Node list, please use
	 * getAllXMPPPubSubNodeList method
	 */
	public List<String> getAllSensorList(String targetSoxServer)
			throws NoResponseException, XMPPErrorException,
			NotConnectedException {

		if (!pubsubManagers.containsKey(targetSoxServer)) {
			addPubSubManager(targetSoxServer);
		}

		PubSubManager manager = pubsubManagers.get(targetSoxServer);

		DiscoverItems items = manager.discoverNodes(null);
		List<Item> nodeList = items.getItems();
		List<String> sensorList = new ArrayList<String>();

		for (Item node : nodeList) {
			// TODO: this is only checking _meta node. Ideally, the method
			// should check _data and _meta node combination.
			if (node.getNode().endsWith("_meta")) {
				String name = node.getNode().substring(0,
						(node.getNode()).length() - 5); // removing _meta string
				sensorList.add(name);
			}
		}
		return sensorList;
	}

	/**
	 * return all sensor node list from login SOX server.
	 * 
	 * @return
	 * @throws NoResponseException
	 * @throws XMPPErrorException
	 * @throws NotConnectedException
	 */
	public List<String> getAllSensorList() throws NoResponseException,
			XMPPErrorException, NotConnectedException {
		return getAllSensorList(service);
	}

	/*
	 * return all atomic XMPP PubSub Node List (e.g., exampleNode_meta, exampleNode_data...)
	 * This is for testing.
	 */
	public List<String> getAllXMPPPubSubNodeList() throws NoResponseException,
			XMPPErrorException, NotConnectedException {

		PubSubManager manager = pubsubManagers.get(service);

		DiscoverItems items = manager.discoverNodes(null);
		List<Item> nodeList = items.getItems();
		List<String> sensorList = new ArrayList<String>();

		for (Item node : nodeList) {
			String name = node.getNode();
			sensorList.add(name);
		}
		return sensorList;
	}

	
	/*
	 * Checking sensor is exist in the SOX server.
	 * TODO: this relies on getAllSensorList which checks only _meta node.
	 * This means not checking _data node is exist or not.
	 */
	public boolean isSensorExist(String nodeName, String targetSOXServer) {
		try {
			List<String> lists = getAllSensorList(targetSOXServer);
			if (lists.contains(nodeName)) {
				return true;
			}
			return false;

		} catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isSensorExist(String nodeName) {
		return isSensorExist(nodeName,service);
	}
	
	
	
	/*
	 * For managing pubsub manager to enable SOX federation
	 */

	/*
	 * Add target sox server to hashtable.
	 */
	public void addPubSubManager(String targetServer) {
		pubsubManagers.put(targetServer, new PubSubManager(con, "pubsub."
				+ targetServer));
	}

	/*
	 * get current login server's pubsubmanager
	 */
	public PubSubManager getPubSubManager() {
		PubSubManager manager = pubsubManagers.get(service);
		return manager;
	}

	/*
	 * get another server's pubsubmanager. This is for SOX federation.
	 */
	public PubSubManager getPubSubManager(String targetServer) {
		if (!pubsubManagers.containsKey(targetServer)) {
			this.addPubSubManager(targetServer);
		}
		PubSubManager manager = pubsubManagers.get(targetServer);
		return manager;
	}

	
	
	/*
	 * Getter
	 */
	public XMPPConnection getXMPPConnection() {
		return con;
	}

	public String getJID() {
		return jid;
	}

	public String getServerName() {
		return server;
	}

	public String getServiceName() {
		return service;
	}

}
