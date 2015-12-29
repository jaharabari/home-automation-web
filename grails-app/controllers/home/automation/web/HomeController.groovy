package home.automation.web

import grails.converters.JSON
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class HomeController {

    def grailsApplication

    def index() {

    }

    def chartData() {
        def name = params.switchValue.equals('Umidade') ? 'humidity' : 'temperature'
        def list = SensorData.executeQuery('select sd from SensorData as sd where sd.name = :name order by sd.dateCreated asc', [name: name], [max: 25]).collect{
            [value: it.valueOf, time: it.dateCreated.time]
        }
        def result = [error: 0, payload: list]
        render result as JSON
    }

    def publish() {
        def topic = "lights/room1"
        def content = "0"
        def qos = 0
        def persistence = new MemoryPersistence()

        try {
            def client = new MqttClient((String) grailsApplication.config.grails.mqtt.brokerUrl,
                    (String) grailsApplication.config.grails.mqtt.clientId,
                    persistence)
            def connOpts = new MqttConnectOptions()
            connOpts.setCleanSession(true)

            println "Connecting to broker: ${grailsApplication.config.grails.mqtt.brokerUrl}"
            client.connect(connOpts)
            println "Connected"

            println "Publishing message: ${content}"
            def message = new MqttMessage(content.getBytes())
            message.setQos(qos)
            client.publish(topic, message)
            println "Message published"

            client.disconnect()
        } catch(MqttException me) {
            me.printStackTrace()
        }
    }
}
