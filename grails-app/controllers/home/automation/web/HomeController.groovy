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
        def temperature = 0
        def humidity = 0

        def temperatures = SensorData.executeQuery('select sd from SensorData as sd where sd.name = :name order by sd.dateHappened desc', [name: 'temperature'], [max: 1])
        if (!temperatures.isEmpty())
            temperature = temperatures.get(0).valueOf

        def humidities = SensorData.executeQuery('select sd from SensorData as sd where sd.name = :name order by sd.dateHappened desc', [name: 'humidity'], [max: 1])
        if (!humidities.isEmpty())
            humidity = humidities.get(0).valueOf

        [temperature: temperature, humidity: humidity]
    }

    def charts() {
    }

    def chartData() {
        def start = params.date('start', 'dd/MM/yyyy HH:mm')
        def end = params.date('end', 'dd/MM/yyyy HH:mm')
        def temperatures = SensorData.executeQuery('select sd from SensorData as sd where sd.name = :name and sd.dateHappened >= :start and sd.dateHappened <= :end order by sd.dateHappened asc', [name: 'temperature', start: start, end: end], [max: 500]).collect{
            [value: it.valueOf, time: it.dateHappened.format('dd/MM/yy HH:mm:ss')]
        }
        def humidities = SensorData.executeQuery('select sd from SensorData as sd where sd.name = :name and sd.dateHappened >= :start and sd.dateHappened <= :end order by sd.dateHappened asc', [name: 'humidity', start: start, end: end], [max: 500]).collect{
            [value: it.valueOf, time: it.dateHappened.format('dd/MM/yy HH:mm:ss')]
        }
        def result = [error: 0, payload: [temperatures: temperatures, humidities: humidities]]
        render result as JSON
    }

    def publish() {
        def topic = params.topic
        def content = params.message
        def qos = 0
        def persistence = new MemoryPersistence()

        try {
            def client = new MqttClient((String) grailsApplication.config.localmqtt.host + ":" +
                    (String) grailsApplication.config.localmqtt.port,
                    (String) grailsApplication.config.grails.mqtt.clientId,
                    persistence)
            def connOpts = new MqttConnectOptions()
            connOpts.setCleanSession(true)
            client.connect(connOpts)
            def message = new MqttMessage(content.getBytes())
            message.setQos(qos)
            client.publish(topic, message)
            client.disconnect()

            def result = [error: 0, payload: '']
            render result as JSON
        } catch(MqttException me) {
            me.printStackTrace()

            def result = [error: 1, payload: me.message]
            render result as JSON
        }
    }
}
