# These commands might be Linux(or MacOS specific)

clean: 
	rm -rf client/*.class && rm -rf server/*.class && rm -rf shared/*.class

compile:
	javac client/*.java server/*.java shared/*.java

server: compile
	java server.RunMessenger

client: compile
	java client.RunClient