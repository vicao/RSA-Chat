all: 
build:
	javac MiniRSA.java

runserver:
	python server.py 29292

run:
	python server.py 29292

runclient:
	python client.py 29292 

clean: 
	rm -f rsa.pyc
	rm -f client.pyc
	rm -f server.pyc
	rm -f MiniRSA.class
	rm -f core
