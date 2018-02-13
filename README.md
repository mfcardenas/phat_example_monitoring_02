# PHAT Example Monitoring Activity [01]
Patient performs basic activities while suffering tremors in hands and neck.
 
While performing the activities this is monitored with acceleration sensors in both hands.
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
        <img src="https://github.com/mfcardenas/phat_example_monitoring_01/blob/master/img/img_older_people_home.png" />
    </td>
</tr>
</table>