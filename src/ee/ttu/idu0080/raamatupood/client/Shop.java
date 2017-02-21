package ee.ttu.idu0080.raamatupood.client;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import ee.ttu.idu0080.raamatupood.server.EmbeddedBroker;
import ee.ttu.idu0080.raamatupood.types.Tellimus;
import ee.ttu.idu0080.raamatupood.types.TellimuseRida;
import ee.ttu.idu0080.raamatupood.types.Toode;

public class Shop implements ExceptionListener, MessageListener {

	private static final String RESPONSE_QUEUE_NAME = "tellimus.vastamine";

	public static void main(String[] args) throws JMSException {
		Shop shop = new Shop();

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(EmbeddedBroker.URL);
		Connection connection = connectionFactory.createConnection();
		connection.setExceptionListener(shop);
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Queue responseQueue = session.createQueue(RESPONSE_QUEUE_NAME);
		MessageConsumer consumer = session.createConsumer(responseQueue);
		consumer.setMessageListener(shop);

		Queue requestQueue = session.createQueue("tellimus.edastamine");
		MessageProducer producer = session.createProducer(requestQueue);

		Tellimus tellimus = new Tellimus();
		tellimus.add(new TellimuseRida(Toode.MINT_TEA, 100));
		tellimus.add(new TellimuseRida(Toode.CEYLON_TEA, 200));
		ObjectMessage message = session.createObjectMessage(tellimus);
		producer.send(message);
		System.out.println("Sent: " + tellimus.getText());

		tellimus = new Tellimus();
		tellimus.add(new TellimuseRida(Toode.MACHI_TEA, 50));
		message = session.createObjectMessage(tellimus);
		producer.send(message);
		System.out.println("Sent: " + tellimus.getText());
		
		tellimus = new Tellimus();
		tellimus.add(new TellimuseRida(Toode.MACHI_TEA, 10));
		tellimus.add(new TellimuseRida(Toode.JAPANESE_GREEN_TEA, 10));
		tellimus.add(new TellimuseRida(Toode.MINT_TEA, 500));
		message = session.createObjectMessage(tellimus);
		producer.send(message);
		System.out.println("Sent: " + tellimus.getText());
	}

	@Override
	public void onException(JMSException arg0) {
		arg0.printStackTrace();
	}

	@Override
	public void onMessage(Message arg0) {
		if (!(arg0 instanceof TextMessage)) {
			System.err.println("Received message that is not a text message.");
			return;
		}

		TextMessage textMessage = (TextMessage) arg0;
		try {
			String messageText = textMessage.getText();
			System.out.println(RESPONSE_QUEUE_NAME + ": " + messageText);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
