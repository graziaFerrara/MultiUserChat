# MultiUserChat

A multi-user chat server listens on a specific TCP port (33333 in this case). The server allows multiple clients to connect concurrently.
Each client is able to send messages to the server, and the server can broadcast these messages to all other connected clients or to a specific client.
The serverhandles the following functionalities:
1. Accept new connections from clients.
2. Handle client disconnections.
3. Forward messages from one client to all other connected clients (BROADCAST) or to a single user (ONE-TO-ONE)
4. Display messages sent by clients in the server's console.
The client sllows to:
1. Connect to the server using its IP address and port number.
2. Allow the user to input a username.
3. Allow the user to send messages to all other connected clients (BROADCAST) or to a single user (ONE-TO-ONE).
4. Display messages received from other clients.

A basic GUI is provided for both the clients and the server.

To run the project in Eclipse make sure to:
• install the package e(fx)clipse from the marketplace (Help > Eclipse Marketplace), or if you have one of the more recent Java versions, it's better to download its nightly version directly from their repository to avoid the bug verifying when creating a FXML document
• download the JavaFX SDK (I used the 21) https://gluonhq.com/products/javafx/
• in Preferences > JavaFX set the path to the lib folder of the SDK
• in Preferences > User Libraries create a new library (it's sufficient that it contains the jars from the lib folder of the SDK)
• right click on the project and in Build Path > Configure BuildPath > Libraries > Modulepath add the just created library
• during the execution, in Run Configurations > Arguments > VM arguments, insert --module-path="/Library/Frameworks/javafx-sdk-21/lib" --add-modules=javafx.controls,javafx.fxml putting your path to the SDK,
• to avoid problems when running, uncheck the option "Use the -XstartOnFirstThread argument when launching with SWT" (for both the server and the client)

