# speedtest-cli
Just a little program, for testing internet connectivity. Shows download speed in mbps, when it's done, and ping, and a progress bar during download.

Requires java to run - made using java 23 - but should run on every java from v8 and up.

Can be run with the following arguments:

* h, help, -h, for a little help dialogue.
```
java -jar Speedtest.jar h
```
* 10, for downloading a 10MB test file (smallest)
```
java -jar Speedtest.jar 10
```
* 100, for downloading a 100MB test file
* 500, for downloading a 512MB test file
* 1000, for downloading a 1GB test file
* Any valid URL to a file, should also work.

 Note that the program does not save the file - it just downloads it to the buffer, shows the result and deletes it.


Test with a 30MB video file:
```
java -jar Speedtest.jar https://sample-videos.com/video321/flv/360/big_buck_bunny_360p_30mb.flv
```

![speedtest](https://github.com/user-attachments/assets/863a56ea-ef31-48fd-8052-8485dc65ec2d)
