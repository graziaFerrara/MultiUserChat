# MultiUserChat

A <b>multi-user chat server</b> listens on a specific <b>TCP</b> port (33333 in this case). The server allows multiple clients to connect concurrently.
Each client is able to send messages to the server, and the server can broadcast these messages to all other connected clients or to a specific client.
<br/>
The server handles the following functionalities:
<ol>
  <li>Accept new connections from clients.</li>
  <li>Handle client disconnections.</li>
  <li>Forward messages from one client to all other connected clients (<b>BROADCAST</b>) or to a single user (<b>ONE-TO-ONE</b>)</li>
  <li>Display messages sent by clients in the server's console.</li>
</ol>
<br/>
The client allows to:
<ol>
  <li>Connect to the server using its IP address and port number.</li>
  <li>Allow the user to input a username.</li>
  <li>Allow the user to send messages to all other connected clients (<b>BROADCAST</b>) or to a single user (<b>ONE-TO-ONE</b>).</li>
  <li>Display messages received from other clients.</li>
</ol> 
<br/>
A basic GUI is provided for both the clients and the server.
<br/>
<hr/>
To run the project in the Eclipse IDE make sure to:
<ul>
  <li>install the package <i>e(fx)clipse</i> from the marketplace <i>(Help > Eclipse Marketplace)</i>, or if you have one of the more recent Java versions, it's better to download its nightly version directly from their repository to avoid the bug verifying when creating a FXML document</li>
  <li>download the JavaFX SDK (I used the 21) <a href="https://gluonhq.com/products/javafx/" target="_blank" >here</a></li>
  <li>in <i>Preferences > JavaFX</i> set the path to the lib folder of the SDK</li>
  <li>in <i>Preferences > User Libraries</i> create a new library (it's sufficient that it contains the jars from the lib folder of the SDK)</li>
  <li>right click on the project and in <i>Build Path > Configure BuildPath > Libraries > Modulepath</i> add the just created library</li>
  <li>during the execution, in <i>Run Configurations > Arguments > VM arguments</i>, insert <i>--module-path="/Library/Frameworks/javafx-sdk-21/lib" --add-modules=javafx.controls,javafx.fxml</i> putting your path to the SDK,</li>
  <li>to avoid problems when running, uncheck the option <i>"Use the -XstartOnFirstThread argument when launching with SWT"</i> (for both the server and the client)</li>
</ul>

