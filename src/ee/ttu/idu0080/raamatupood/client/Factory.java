package ee.ttu.idu0080.raamatupood.client;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

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

public class Factory implements ExceptionListener, MessageListener {

	public static void main(String[] args) throws JMSException {
		Factory factory = new Factory();

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(EmbeddedBroker.URL);
		Connection connection = connectionFactory.createConnection();
		connection.setExceptionListener(factory);
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue recvQueue = session.createQueue("tellimus.edastamine");
		MessageConsumer consumer = session.createConsumer(recvQueue);
		consumer.setMessageListener(factory);

		Queue sendQueue = session.createQueue("tellimus.vastamine");
		MessageProducer producer = session.createProducer(sendQueue);

		factory.setFields(producer, session);
	}

	private MessageProducer producer;
	private Session session;

	public void setFields(MessageProducer producer, Session session) {
		this.producer = producer;
		this.session = session;
	}

	@Override
	public void onException(JMSException arg0) {
		arg0.printStackTrace();
		if (producer != null) {
			try {
				producer.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		if (session != null) {
			try {
				session.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onMessage(Message arg0) {
		if (!(arg0 instanceof ObjectMessage)) {
			System.err.println("Message is not a ObjectMessage.");
			return;
		}

		ObjectMessage objectMessage = (ObjectMessage) arg0;
		try {
			Serializable object = objectMessage.getObject();
			if (!(object instanceof Tellimus)) {
				System.err.print("Object of the message is not a Tellimus.");
				return;
			}

			Tellimus tellimus = (Tellimus) object;

			Set<Toode> tellitudTooted = new TreeSet<Toode>();
			BigDecimal hindKokku = BigDecimal.ZERO;
			for (TellimuseRida rida : tellimus) {
				tellitudTooted.add(rida.toode);

				hindKokku = hindKokku.add(new BigDecimal(rida.kogus).multiply(rida.toode.hind));
			}

			TextMessage textMessage = session.createTextMessage(
					String.format("Tooteid tellitud: %s, hind kokku: %s.", tellitudTooted.size(), hindKokku));
			
			Object lock = new Object();
			synchronized (lock) {
				lock.wait(2 * 1000);
			}
			producer.send(textMessage);
			System.out.println("Sent: " + textMessage.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
