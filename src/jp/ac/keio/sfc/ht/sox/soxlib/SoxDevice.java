package jp.ac.keio.sfc.ht.sox.soxlib;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import jp.ac.keio.sfc.ht.sox.protocol.Data;
import jp.ac.keio.sfc.ht.sox.protocol.Device;
import jp.ac.keio.sfc.ht.sox.protocol.TransducerValue;
import jp.ac.keio.sfc.ht.sox.soxlib.event.SoxEvent;
import jp.ac.keio.sfc.ht.sox.soxlib.event.SoxEventListener;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.Matcher;
import org.simpleframework.xml.transform.Transform;

public class SoxDevice implements ItemEventListener {

	private SoxConnection con;
	private String pubSubNodeId;
	private Device device;
	private Data lastData;
	private String targetSoxServer;
	private SoxEventListener soxEventListener;
	String dataString;
	private LeafNode eventNode_data;
	private LeafNode eventNode_meta;

	/*
	 * Constructor of SoxDevice. This method implicit specify to use login SOX
	 * server.
	 */
	public SoxDevice(SoxConnection _con, String _pubSubNodeId) throws Exception {

		this(_con, _pubSubNodeId, _con.getServiceName());
	}

	/*
	 * Constructor of SoxDevice. This method specify to use which SOX server.
	 * This is for SOX federation (using another SOX server).
	 */
	public SoxDevice(SoxConnection _con, String _pubSubNodeId,
			String _targetSoxServer) throws Exception {

		con = _con;
		pubSubNodeId = _pubSubNodeId;
		targetSoxServer = _targetSoxServer;

		this.bindXMPPPubSubNodesAsVirtualDevice();
	}

	private void bindXMPPPubSubNodesAsVirtualDevice() throws Exception {

		// getting _meta node and _data node.
		eventNode_meta = con.getPubSubManager(targetSoxServer).getNode(
				pubSubNodeId + "_meta");
		eventNode_data = con.getPubSubManager(targetSoxServer).getNode(
				pubSubNodeId + "_data");

		// Getting meta information which saved to _meta node.
		List<? extends PayloadItem> items = eventNode_meta.getItems(1);
		Serializer serializer = new Persister(new Matcher() {
			public Transform match(Class type) throws Exception {
				if (type.isEnum()) {
					return new SoxEnumTransform(type);
				}
				return null;
			}
		});

		for (PayloadItem item : items) {
			String metaString = item.getPayload().toXML().toString();
			metaString = metaString.replaceAll("&lt;", "<");
			metaString = metaString.replaceAll("&gt;", ">");
			metaString = metaString.replaceAll("&apos;", "'");
			device = serializer.read(Device.class, metaString);
			System.out.println("[info]device created from meta data:"
					+ pubSubNodeId + "_meta");
		}

		// This is for preventing multiple subscribing by same JID.
		List<Subscription> subscriptions = eventNode_meta.getSubscriptions();
		for (Subscription s : subscriptions) {
			eventNode_meta.unsubscribe(s.getJid(), s.getId());
		}

	}

	/*
	 * Checking the node is subscribed by the user.
	 */
	public boolean isSubscribe() {
		try {
			if (eventNode_data == null) {
				eventNode_data = con.getPubSubManager(targetSoxServer).getNode(
						pubSubNodeId + "_data");
			}
			List<Subscription> subscriptions = eventNode_data
					.getSubscriptions();
			for (Subscription s : subscriptions) {

				if (s.getJid().equals(con.getXMPPConnection().getUser())) {
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * Subscribing _data node. Notice: current implementation only assume that
	 * it is not necessary to subscribe _meta node. Because _meta information is
	 * already received.
	 */
	public void subscribe() {

		try {

			if (eventNode_data == null) {
				eventNode_data = con.getPubSubManager(targetSoxServer).getNode(
						pubSubNodeId + "_data");
			} else {
				// if the node is already subscribed by the user, unsubscribe. This is to prevent multiple subscribing by same user.
				List<Subscription> subscriptions = eventNode_data
						.getSubscriptions();
				for (Subscription s : subscriptions) {
					eventNode_data.unsubscribe(s.getJid(), s.getId());
				}

				eventNode_data.addItemEventListener(this);
				//Subscribe. If past data is stored in SOX server, event listener will be called immediately.
				eventNode_data.subscribe(con.getXMPPConnection().getUser());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * Unsubscribing _data node. Notice: current implementation only assume that
	 * it is not necessary to unsubscribe _meta node. Because _meta node is usually not subscribed.
	 */
	public void unsubscribe() {
		try {
			List<Subscription> subscriptions = eventNode_data
					.getSubscriptions();

			eventNode_data.removeItemEventListener(this);
			
			for (Subscription s : subscriptions) {
				eventNode_data.unsubscribe(s.getJid(), s.getId());
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/*
	 * Notice: this is cheating solution.
	 * Current data node does not save persistent data in SOX server.
	 * So, to get last data, we have to subscribe first then unsubscribe after getting data.
	 */
	public Data getLastPublishData() throws Exception {

		/** For _data node which have persist item in XMPP server **/
		
		List<? extends PayloadItem> items = eventNode_data.getItems(1);
		Serializer serializer = new Persister(new Matcher() {
			public Transform match(Class type) throws Exception {
				if (type.isEnum()) {
					return new SoxEnumTransform(type);
				}
				return null;
			}
		});

		for (PayloadItem item : items) {
			dataString = item.getPayload().toXML().toString();
			dataString = dataString.replaceAll("&lt;", "<");
			dataString = dataString.replaceAll("&gt;", ">");
			dataString = dataString.replaceAll("&apos;", "'");
			System.out.println(dataString);
			lastData = serializer.read(Data.class, dataString);
		}


		
		/** For _data node which do not have persist item in XMPP server **/
		/**
		boolean wasSubscribed = true;

		if (!isSubscribe()) {
			this.subscribe();
			wasSubscribed = false;
		}
		int time = 0;
		int waitingTime = 50;
		while (lastData == null) {
			Thread.sleep(waitingTime);
			time = time + waitingTime;
			if (time > 500) { //only waiting 500 milliseconds. If network is so slow, it cannot get last published data. This is current limitation.
				break; // in this case, null data will be sent.
			}
		}

		if (lastData == null) {
			System.out.println("No Last Published Data..");
		}
		if (!wasSubscribed) {
			this.unsubscribe();
		}
		**/
		
		return lastData;

	}
	
	
	/*
	 * Publishing Values.
	 */
	public void publishValues(List<TransducerValue> values) {

		if (eventNode_data == null) {
			try {
				eventNode_data = con.getPubSubManager(targetSoxServer).getNode(
						pubSubNodeId + "_data");
				;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Data data = new Data();
		if (values != null) {
			data.setTransducerValue(values);
		}
		
		// transform data object into XML string
		StringWriter writer = new StringWriter();
		Persister serializer = new Persister(new Matcher() {
			public Transform match(Class type) throws Exception {
				if (type.isEnum()) {
					return new SoxEnumTransform(type);
				}
				return null;
			}
		});

		try {
			serializer.write(data, writer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Creating publish item
		SimplePayload payload = new SimplePayload(pubSubNodeId,
				"http://jabber.org/protocol/sox", writer.toString());

		PayloadItem<SimplePayload> pi = new PayloadItem<SimplePayload>(null,
				payload);
		//System.out.println(pi.getPayload().toXML());
		// Publish
		try {

			eventNode_data.publish(pi);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	// publich singple transducerValue
	public void publishValue(TransducerValue value) {

		List<TransducerValue> valueList = new ArrayList<TransducerValue>();
		valueList.add(value);
		this.publishValues(valueList);

	}

	
	public void publishMeta(Device _device){

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
			serializer.write(_device, writer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Creating publish item
		SimplePayload payload = new SimplePayload(pubSubNodeId,
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

	}
	
	
	@Override
	public void handlePublishedItems(ItemPublishEvent event) {
		// TODO Auto-generated method stub
				List<PayloadItem> items = event.getItems();

				Serializer serializer = new Persister();
				for (PayloadItem item : items) {
					// System.out.println("item:"+item.getPayload().toXML());
					try {

						System.out.println("[info]data received from data node:"
								+ pubSubNodeId + "_data");

						dataString = item.getPayload().toXML().toString();
						dataString = dataString.replaceAll("&lt;", "<");
						dataString = dataString.replaceAll("&gt;", ">");
						dataString = dataString.replaceAll("&apos;", "'");

						Data data = serializer.read(Data.class, dataString);
						lastData = data;

						List<TransducerValue> list = data.getTransducerValue();
						
						if (soxEventListener != null) {
							soxEventListener
									.handlePublishedSoxEvent(new SoxEvent(
											this, device, list));
						}


					} catch (Exception e) {
						e.printStackTrace();
					}
				}

	}

	
	// Getter
	public String getNodeId() {
		return pubSubNodeId;
	}

	public Device getDevice() {
		return device;
	}

	public String getPubSubServerName() {
		return targetSoxServer;
	}

	// For Event Listener
	public void addSoxEventListener(SoxEventListener _listener) {
		soxEventListener = _listener;
	}

	public void removeSoxEventListener() {
		soxEventListener = null;
	}
	
}
