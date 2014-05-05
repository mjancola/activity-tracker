# Activity Tracker

An Intelligent Exercise Tracking Application for Android

Villanova Grand Challenges Project
Spring 2014

Description:
Many smartphone apps exist on the market to track individual walks, runs and bike rides. Is it possible to have just one app to track all your exercise? My Grand Challenges project is a Proof of Concept mobile application (native Android), designed to learn and track when you are exercising. The goal of the project is to determine if it is possible to model and track user activity without consuming the battery in a typical day. The project will begin with a high level project summary with timeline and milestones. The project will conclude with a report summarizing the result and major design issues.

## Setup
*Note: These instructions assume you have a Java 1.6 [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) installed.*
*Note: These instructions rely on the Community version of Intellij*

1. Install the [Android SDK](http://developer.android.com/sdk/index.html). On Mac OS X with [Homebrew](http://brew.sh/) just run:
    ```bash
    brew install android-sdk
    ```

2. Set your `ANDROID_HOME` environment variable to `/usr/local/opt/android-sdk`.

3. Run the Android SDK GUI and install API 18 and any other APIs you might need. You can start the GUI like so:
    ```bash
    android
    ```
4. Install Maven if you haven't already (run `mvn` to check). On OS X (as before) this is easiest with [Homebrew](http://brew.sh/):
	```bash
	brew install maven
	```
	* If you are planning on using Android SDK >=4.4, you need maven >=3.1.1.  Instuctions for its installation are available [here](http://myjeeva.com/how-to-do-maven-upgrade-in-mac-os-x.html).

5. Use [Maven Android SDK Deployer](https://github.com/mosabua/maven-android-sdk-deployer) to maven-ize the Android SDK:
    ```bash
    git clone https://github.com/mosabua/maven-android-sdk-deployer.git
    (cd maven-android-sdk-deployer && mvn install -P 4.3)
    ```

6. Import the project into IntelliJ (or Eclipse) by selecting 'Import Project' in IntelliJ and selecting the project's `pom.xml`. When prompted to pick an SDK you just need to select the Android SDK home and your JDK.

