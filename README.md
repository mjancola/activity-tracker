# Activity Tracker

An Intelligent Exercise Tracking Application for Android

Villanova Grand Challenges Project
Spring 2014

Description:
Many smartphone apps exist on the market to track individual walks, runs and bike rides. Is it possible to have just one app to track all your exercise? My Grand Challenges project is a Proof of Concept mobile application (native Android), designed to learn and track when you are exercising. The goal of the project is to determine if it is possible to model and track user activity without consuming the battery in a typical day. The project will begin with a high level project summary with timeline and milestones. The project will conclude with a report summarizing the result and major design issues.

## Setup
*Note: These instructions assume you have a Java 1.6 [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) installed.*
*Note: These instructions rely on the Community version of Intellij*
*Note: These instructions used on Ubuntu Linux*

1. download Intellij
  extract tgz file to a local folder
  create shortcut/launcher to bin/idea.sh
    get icon file from same folder

2. download Android SDK ONLY
  extract
  execute tools\android
  install all of android 4.0, or something that supports a phone you have

3. install maven 3.1.1 by extracting tarball

4.  sudo /etc/profile and add the following
   export JAVA_HOME=/usr/lib/jvm/java-6-sun
   export ANDROID_HOME=/home/mjancola/pkg/android-sdk-inux
   export PATH=/home/mjancola/pkg/android-sdk-linux/platform-tools:/home/mjancola/pkg/android-sdk-linux/tools:/usr/lib/jvm/java-6-sun/bin:/home/mjancola/pkg/apache-ant-1.9.3/bin:/home/mjancola/pkg/apache-maven-3.1.1/bin:$PATH


5. (optional) validate setup by launching Intellij and building a new empty project
  create new project, specifiy:
    name
    paths
    select create module
    Android Project as type
    Click Next, Next (ok to create src folder)
    On the select Java SDK page, select the Android SDK you installed earlier

6. (optional) install the test project on a phone
  if devices don't show up in Intellij when connected Via USB and Dev mode enabled on device:
    cd ~/pkg/android-sdk-linux/platform-tools/
    sudo ./adb kill-server
    sudo ./adb start-server
    sudo ./adb devices
      if you don't see your device, stop and troubleshoot

7. From IntelliJ, install all necessary packages.  Select Tools>Android>SDK
    install Extras\Android Support Library, Android SDK Tools, and Android SDK Platform-tools
    Android SDK Tools (v15)
    Android Build-Tools (19.0.3)
    android 4.1.2 (16): SDK Platform, ARM image, Google APIs
    android 4.1.2 (16): SDK Platform, ARM image, Google APIs
    android 4.0.3 (15): SDK Platform, ARM image, Google APIs
    android 4.0 (14): SDK Platform, ARM image, Google APIs
    android 2.3.3 (16): SDK Platform, ARM image, Google APIs
    Android Support Library
    Android Play Services
    -> rerun SDK installer if prompted, exit

8. Install Android to maven repo
  mvn install -Dextras.compatibility.v4.groupid=com.google.android -Dextras.compatibility.v4.artifactid=support-v4 -Dextras.compatibility.v4.version.prefix=r
   
9.  get project from GitHub:
  cd source
  git clone https://github.com/mjancola/activity-tracker.git  (URL link from website)

10. ADD GOOGLE PLAY SERVICES to LOCAL MAVEN REPO
  Copy the gms-mvn-install.sh script (from the project root) into the extras/google/google_play_services/libproject/google-play-services_lib folder inside of your SDK.
  Execute ./gms-mvn-install.sh 14 which will install the project into your local Maven repository. 

			Now this dependency in the project Manifest will work
			<dependency>
			  <groupId>com.google.android.gms</groupId>
			  <artifactId>google-play-services</artifactId>
			  <version>14</version>
			  <type>apklib</type>
			</dependency>

11. swap out values in AndroidManifest.xml
      	<meta-data
                android:name="com.google.android.gms.version"
                android:value="4323000"/> 
12. launch IntelliJ
  Import Project
  From Maven
  click Next through defaults
  select Android 4.0.3 as the SDK library
  repeat for Google services library module
  Click Libraries
    Add, from Java, support V4, select the .JAR file in the extras support folder
      associate new library as dependency of the main application module 

13. F4 on the 'activity-tracker' project name
  Under depenedencies of the main activity-tracker app:
    change maven android support v4 to provided
    be sure android support v4 is compiled


  




