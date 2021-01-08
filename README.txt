DeliveryDrone:
To run our project:
* Create a Just-In-Time controller for the DeliveryDroneMain.spectra specification
* Run App.java as java program
* At startup, you'll be requested to enter the specification features used by the controller:
** If created a controller for the Main spec file (or variants not specified below) - enter 'Default' option,
** For other variants, check out the comments at the top of the file to be sure which features are used.
** If commented out specific features from the specification on your own - specify which features are indeed used.
* The simulation should be running (manual mode)

To control the simulation:
* At the right menu panel, simply choose which package/envelope delivery request you'd like to add.
** From house : add pickup from House #X to the Warehouse (this can be a package or an envelope).
** From warehouse : add pickup from Warehouse to House #X
** You can also add a random request - can be a package or an envelope at it's source/destination is also randomized.
* You can toggle Priority Mode to be ON/OFF.
* You can toggle Winds Mode to be ON/OFF.
* You can run Demo Mode - which is a random automated environment.
* You can run Scenarios - Semi-Automatic "scripts" of environment behaivor.
* When a Scenario is activated, you can click on the 'Fast Forward Step' button to speed up the step's completion.

Read the simulation details:
* Generally, At each house you can see:
** If it has a package/envelope waiting for pickup (LHS of the house)
** If it's waiting package/envelope was picked up (It disappears and there's a indication at the drone control panel).
** Whether package was dropped-off at this house (RHS of the house)

* Warehouse Details:
** Waiting packages can be viewed at the Warehouse board - by house # destination.

In addition, you can view the drone's state details at the control panel:
* Stocking : Whether it picks up a package/evnelope this state (green up arrow), or dropping off a package/envelope (red down arrow).
* Inventory: 
** How many packages the drone has in it's inventory at this state.
** Specifies the packages distribution across all destinations.
** Envelopes inventory.
* Priority mode is toggled ON when a green light is shown, and is OFF when a red light is shown.
* Energy : the drone's battery can be viewed at the RHS of the control panel.