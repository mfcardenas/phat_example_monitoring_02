/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phat;
import java.util.logging.Logger;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import phat.app.PHATApplication;
import phat.app.PHATInitAppListener;
import phat.body.BodiesAppState;
import phat.body.commands.PlayBodyAnimationCommand;
import phat.body.commands.SetBodyInCoordenatesCommand;
import phat.body.commands.SetSpeedDisplacemenetCommand;
import phat.devices.DevicesAppState;
import phat.devices.commands.CreateAccelerometerSensorCommand;
import phat.devices.commands.SetDeviceOnPartOfBodyCommand;
import phat.environment.SpatialEnvironmentAPI;
import phat.sensors.accelerometer.AccelerometerControl;
import phat.sensors.accelerometer.XYAccelerationsChart;
import phat.server.ServerAppState;
import phat.server.commands.ActivateAccelerometerServerCommand;
import phat.structures.houses.HouseFactory;
import phat.structures.houses.commands.CreateHouseCommand;
import phat.world.WorldAppState;

/**
 * Class example Test rum simulatios.
 * <br/>
 * Interval Execute Classificator<br/>
 * float timeToChange = 10f;<br/>
 * ...<br/>
 * if (cont > timeToChange && cont < timeToChange + 1 && !fall) {<br/>
 * ...<br/>
 * if (fall && cont > timeToChange + 10) {<br/>
 * <br/>
 * Interval Execute Capture Data<br/>
 * float timeToChange = 2f;<br/>
 * ...<br/>
 * if (cont > timeToChange && cont < timeToChange + 1 && !fall) {<br/>
 * ...<br/>
 * if (fall && cont > timeToChange + 2) {<br/>
 * <br/>
 * Optimal min distance between animation<br/>
 * 0.1 * 0.1 * 0.2 <br/>
 * <br/>
 * <b>Gesture</b>
 * SpinSpindle: 	abrir puerta con dificultad<br/>
 * Hands2Hips: 		llevar manos a la cadera, (dolor de espalda)<br/>
 * Hand2Belly: 		llevar la mano al vientre, (dolor de vientre)<br/>
 * Wave: 			pedir ayuda o llamar atención<br/>
 * ScratchArm: 		rascar el codo<br/>
 * LeverPole: 		molestias en el movimiento y pedir ayuda   <br/>
 * @author UCM
 */
public class ActvityMonitoringDemo implements PHATInitAppListener {

    private static final Logger logger = Logger.getLogger(ActvityMonitoringDemo.class.getName());
    BodiesAppState bodiesAppState;
    ServerAppState serverAppState;
    DevicesAppState devicesAppState;
    WorldAppState worldAppState;


    @Override
    public void init(SimpleApplication app) {
        logger.info("init");
        AppStateManager stateManager = app.getStateManager();

        app.getFlyByCamera().setMoveSpeed(10f);

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(false);

        SpatialEnvironmentAPI seAPI = SpatialEnvironmentAPI.createSpatialEnvironmentAPI(app);

        seAPI.getWorldAppState().setCalendar(2016, 6 ,1, 12, 30, 0);
        seAPI.getHouseAppState().runCommand(new CreateHouseCommand("House1", HouseFactory.HouseType.House3room2bath));

        bodiesAppState = new BodiesAppState();
        stateManager.attach(bodiesAppState);

        //Se crean los personajes
        bodiesAppState.createBody(BodiesAppState.BodyType.Young, "Patient");

        //Se posicionan en la casa
        bodiesAppState.setInSpace("Patient", "House1", "Chair3");

        bodiesAppState.runCommand(new SetBodyInCoordenatesCommand("Patient", Vector3f.ZERO));
        bodiesAppState.runCommand(new SetSpeedDisplacemenetCommand("Patient", 0.5f));

        devicesAppState = new DevicesAppState();
        stateManager.attach(devicesAppState);

        // Positions of sensor Acelerometers
        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor1"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "sensor1",
                SetDeviceOnPartOfBodyCommand.PartOfBody.LeftHand));

        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor2"));
        devicesAppState.runCommand(new SetDeviceOnPartOfBodyCommand("Patient", "sensor2",
                SetDeviceOnPartOfBodyCommand.PartOfBody.RightHand));

        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor3"));
        devicesAppState.runCommand(new CreateAccelerometerSensorCommand("sensor4"));

        serverAppState = new ServerAppState();
        stateManager.attach(serverAppState);

        // Activate Acelerometers
        serverAppState.runCommand(new ActivateAccelerometerServerCommand("PatientBodyAccel", "sensor1"));
        serverAppState.runCommand(new ActivateAccelerometerServerCommand("PatientBodyAccel", "sensor2"));

        app.getCamera().setLocation(new Vector3f(6.5472484f, 1.5681753f, 4.951794f));
        app.getCamera().setRotation(new Quaternion(0.030681776f, 0.9156617f, -0.0712238f, 0.39439762f));

        stateManager.attach(new AbstractAppState() {
            PHATApplication app;

            @Override
            public void initialize(AppStateManager asm, Application aplctn) {
                app = (PHATApplication) aplctn;

            }

            float cont = 0f;
            boolean fall = false;
            float timeToChange = 10f;
            boolean init = false;
            boolean traindata = false;

            @Override
            public void update(float f) {
                if (!init && !traindata) {
                    // Grafica del Sensor2
                    AccelerometerControl ac2 = devicesAppState.getDevice("sensor2")
                            .getControl(AccelerometerControl.class);
                    ac2.setMode(AccelerometerControl.AMode.GRAVITY_MODE);
                    XYAccelerationsChart chart2 = new XYAccelerationsChart("Data Accelerations Rigth Hand", "Local Sensor PHAT-SIM Rigth Hand", "m/s2",
                            "x,y,z");
                    ac2.add(chart2);
                    chart2.showWindow();
                    init = true;

                    // Grafica del Sensor1
                    AccelerometerControl ac1 = devicesAppState.getDevice("sensor1")
                            .getControl(AccelerometerControl.class);
                    ac1.setMode(AccelerometerControl.AMode.GRAVITY_MODE);
                    XYAccelerationsChart chart1 = new XYAccelerationsChart("Data Accelerations Left Hand", "Local Sensor PHAT-SIM Left Hand", "m/s2",
                            "x,y,z");
                    ac1.add(chart1);
                    chart1.showWindow();
                    init = true;

                    // Grafica del Sensor...

                }

                cont += f;
                if (cont > timeToChange && cont < timeToChange + 1f && !fall) {
                    System.out.println("Change to DrinkStanding:::" + String.valueOf(cont) + "-" + String.valueOf(f));
                    bodiesAppState.runCommand(new PlayBodyAnimationCommand("Patient", "DrinkStanding"));
                    fall = true;
                } else {
                    if (fall && cont > timeToChange + 10f) {
                        System.out.println("Change to WaveAttention:::" + String.valueOf(cont) + "-" + String.valueOf(f));
                        bodiesAppState.runCommand(new PlayBodyAnimationCommand("Patient", "WaveAttention"));
                        fall = false;
                        cont = 0;
                    }
                }
            }
        });



    }

    /**
     * Main Class, executios Test.
     * @param args 0
     */
    public static void main(String[] args) {
        ActvityMonitoringDemo app = new ActvityMonitoringDemo();
        PHATApplication phat = new PHATApplication(app);
        phat.setDisplayFps(false);
        phat.setDisplayStatView(false);
        phat.setShowSettings(false);
        phat.start();
    }
}