DeliveryDroneSprint1:

To run our project:
* Create a Just-In-Time controller for the DeliveryDroneMain.spectra specification
* Run App.java as java program
* The simulation should be running (manual mode)

To control the simulation:
* At the right menu panel, simply choose which package delivery request you'd like to add
** From house : add pickup from House #X to the Warehouse
** From warehouse : add pickup from Warehouse to House #X
* You can toggle Priority Mode to be ON/OFF
* You can toggle Winds Mode to be ON/OFF

Read the simulation details:
* Generally, At each house you can see:
** If it has a package waiting for pickup (LHS of the house)
** If it's waiting package was picked up (It disappears and there's a indication at the drone control panel).
** Whether package was dropped-off at this house (RHS of the house)

* Warehouse Details:
** Waiting packages can be viewed at the Warehouse board - by house # destination.

In addition, now you can view the drone's state details at the conrol panel:
* Stocking : Whether it picks up a package this state (green up arrow), or dropping off a package (red down arrow).
* Inventory: 
** How many packages the drone has in it's inventory at this state
** Specifies the packages distribution across all destinations.
* Priority & Winds : a mode is toggled ON when a green light is shown, and is OFF when a red light is shown.
* Energy : the drone's battery can be viewed at the RHS of the control panel.

