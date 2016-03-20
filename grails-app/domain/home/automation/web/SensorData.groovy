package home.automation.web

class SensorData {

    String name
    Double valueOf = 0
    Date dateHappened

    static mapping = {
        sort "dateHappened"
        version false
    }
}
