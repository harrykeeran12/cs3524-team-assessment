clean: 
	rm -rf client/*.class && rm -rf server/*.class && rm -rf shared/*.class

compile:
	javac client/*.java server/*.java 

server: compile
	java server.RunMessenger

client: compile
	java client.RunClient