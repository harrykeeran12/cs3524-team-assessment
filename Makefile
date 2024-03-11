compileD: 
	javac CGS_D/server/*.java
	javac CGS_D/client/*.java

cleanD: 
	rm -rf CGS_D/client/*.class
	rm -rf CGS_D/server/*.class