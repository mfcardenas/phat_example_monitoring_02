# PHAT Example Monitoring Activity [02]
Use of acceleration sensors and smart phone to monitor basic activities in a user.

The sensors are located in:
- Chest (Smart phone)
- Left hand (Sensor Acceleration)
- Right hand. (Sensor Acceleration)

<table>
<tr>
    <td>  
To run the demo

```
mvn clean compile
mvn exec:java -Dexec.mainClass=phat.ActvityMonitoringDemo
```
In case of running into memory problems
```
export MAVEN_OPTS="-Xmx512m -XX:MaxPermSize=128m"
```
And then run the previous command
    </td>
    <td>
        <img src="https://github.com/mfcardenas/phat_example_monitoring_02/blob/master/img/img_older_people_home.png" />
    </td>
</tr>
</table>