DeliveryDroneSprint1:

To run our project:
* Create a Just-In-Time controller for the DeliveryDroneMain.spectra specification
* Run App.java as java program
* The simulation should be running (manual mode)

To control the simulation:
* At the right menu panel, simply choose which package delivery request you'd like to add
** From house : add pickup from House #X to the Warehouse
** From warehouse : add pickup from Warehouse to House #X

Read the simulation details:
* Generally, At each house you can see:
** If it has a package waiting for pickup
** If it's waiting package was picked up
** Whether package was dropped-off at this house (this also applies to the warehouse)

In addition,

* Top-right corner - General Details:
** State #
** PUTS - PickUpThisState (whether a pickup is made at this state and which kind)
** DOTS - DropOffThisState (whether a drop-off is made at this state and which kind)

* Bottom-right corner - Drone Inventory Details:
** Specifies how many packages the drone has in it's inventory at this state
** Specifies the packages distribution across all destinations.

* Cell left to the Warehouse:
** Status for warehouse delieverys by location
*** Flags: W (Waiting for pickup) PU (Picked up this state)