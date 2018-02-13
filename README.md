# PHAT Example Monitoring Activity [02]
Monitoring of a user with mobile phone acceleration sensors positioned on the chest and both hands while performing basic activities.
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