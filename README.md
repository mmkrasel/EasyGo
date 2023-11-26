<div align="center">
	<img src="https://github.com/Anik228/EasyGo-images/assets/93530067/638032f5-0e7b-4d88-9d14-70e6f32598e9" alt="Logo" width="120" height="120">
	<h2 align="center">EasyGo</h2>
	<h3 align="center">
		Efficient Indoor Navigation Map From Streamlined Dataset with Shortest Path Determination
	</h3>
</div>
<details open>
	<summary>Table of Contents</summary>
	<ol>
		<li>
			<a href="#about-the-project">About The Project</a>
			<ul>
				<li><a href="#preview">Project Preview</a></li>
				<li><a href="#contribution">Contribution</a></li>
				<li><a href="#build">Built With</a></li>
			</ul>
		</li>
		<li>
			<a href="#getting-started">Getting Started</a>
			<ul>
				<li><a href="#prerequisites">Prerequisites</a></li>
				<li><a href="#clone-the-repository">Clone the repository</a></li>
			</ul>
		</li>
		<li>
			<a href="#user-instruction">User Instructions</a>
			<ul>				
				<li><a href="#clone-the-repository">Clone the repository</a></li>
				<li><a href="#run-in-development-mode">Run in development mode</a></li>
				<li><a href="#user-instruction">Use an emulator to launch the app.</a></li>
			</ul>
		</li>
		<li>
			<a href="#developer-instruction">Developer Instructions</a>
			<ul>				
				<li><a href="#run-in-production-mode">Clone the repository</a></li>
				<li><a href="#user-instruction">Browse the app</a></li>
			</ul>
		</li>
		<li><a href="#find-bug">Find Bug?</a></li>
		<li><a href="#issues">Know issues (Work In Progress)</a></li>
		<li><a href="#license">License</a></li>
		<li><a href="#support">Support - Like This Project ?</a></li>
		<li><a href="#contributor">Contributor</a></li>
		<li><a href="#contract">Contract</a></li>
	</ol>
</details>

## About this project

EasyGo is an innovative indoor navigation application designed to streamline data collection and facilitate efficient navigation within indoor spaces. Leveraging mobile sensors such as accelerometer, magnetometer, and gyroscope, EasyGo empowers users to collect comprehensive indoor data effortlessly.

The platform offers an intuitive user interface that allows administrators to seamlessly gather indoor data, enabling swift and precise mapping of indoor environments. Users benefit from an interactive panel, granting them the ability to navigate personalized routes with ease.

At its core, EasyGo prioritizes user experience by providing a robust system for data collection while offering efficient and optimized indoor navigation, ensuring users can traverse indoor spaces conveniently and efficiently.

## Preview

<div align="center">
	<img src="https://github.com/KaziSifatAlMaksud/link/assets/65750595/9f91f2b5-4723-4897-b78a-b6d14b737be1" alt="demo" width="800">
</div>

## Contribution

<ol>
<li>Efficient use of mobile phone sensors (i.e. Accelerometer, Gyroscope, Magnetometer) to collect indoor navigation data.
</li>

<li>Automated detection of invalid data reading from sensors.
</li>

<li>Use efficient data structures (i.e., graph and coordinates) to represent navigation data.
</li>

<li>Find all pairs shortest paths to navigate from door A to B.
</li>

<li>User friendly and simple mobile app UI/UX for indoor navigation.
</li>



 <h2 id="build">Built With</h2>

<table align="center" width="800">
	<tr> 
	    <td align="center">
            <a href="https://www.java.com">
                <img src="https://github.com/Anik228/EasyGo-images/assets/93530067/fefa5301-5166-4993-a49b-3cc2d75e6a58" width="70px;" height="75px;" alt="Java"/><br />
                <b><font color="#777">Java</font></b>
            </a>
        </td>
		<td align="center" ><a href="https://developer.android.com/studio"><img style="border-radius: 8px;" src="https://github.com/KaziSifatAlMaksud/link/assets/65750595/e1564fa2-adb1-47d4-a68c-eb22452dbf76" width="70px;" height="75px;" alt="TypeScript" /><br /><b><font color="#777">Android Studio</font></b></a></td>
		<td align="center"><a href="https://www.mysql.com/about/legal/logos.html"><img src="https://github.com/KaziSifatAlMaksud/link/assets/65750595/6a80364c-af84-4026-8219-8495de53d92f" width="70px;" height="75px;" alt="Next JS"/><br /><b><font color="#777">MySQL</font></b></a></td>
		<td align="center"><a href="https://reactjs.org"><img src="https://github.com/KaziSifatAlMaksud/link/assets/65750595/3ecfc948-9e1a-49d0-b2db-90641ae2d3d7" width="80px;" height="75px;" style="border-radius: 8px;" alt="React JS"/><br /><b><font color="#777">PHP</font></b></a></td>
		<td align="center"><a href="https://www.apollographql.com/"><img src="https://github.com/KaziSifatAlMaksud/link/assets/65750595/a98c0ff2-104e-4ba8-8343-3e935f2d9292" width="75px;" height="75px;" alt="Apollo"/><br /><b><font color="#777">Rest API</font></b></a></td>
		<td align="center"><a href="https://tailwindcss.com/"><img src="https://github.com/KaziSifatAlMaksud/link/assets/65750595/6e42f395-1f3e-4faf-988b-104a2943cc04" width="75px;" height="75px;" alt="Tailwind"/><br /><b><font color="#777">Remote Server</font></b></a></td>
</table>

<!-- ### \* Java

### \* MySQL

### \* PHP -->

<h2 id="getting-started">Getting Started</h2>

#### Prerequisites

- Java
- XML
- MySQL
- Android Studio
- Knowledge of Mobile Sensor
- PHP
- Rest API

 <h2 id="user-instruction">User instruction</h2>

 <p id="run-in-development-mode">Running the Project Locally</p>

To run this Android Studio project locally, follow these steps:

1. <p id="clone-the-repository">Clone this repository to your local machine using the following command:</p>

```bash

https://github.com/frijve99/EasyGo.git

```

2. Install Android Studio if you haven't already. You can download it from [here](https://developer.android.com/studio).

3. Open the project in Android Studio by selecting "Open an existing Android Studio project" and navigating to the project directory.

4. Configure the `local.properties` file with the path to your Android SDK. Create the file if it doesn't exist and add the following lines:

Replace `/path/to/your/android/sdk` with the actual path to your Android SDK.

5. Build the project by clicking on "Build" in the Android Studio toolbar and then selecting "Make Project".

6. Run the project by clicking on the "Run" button (green triangle) in the Android Studio toolbar.

That's it! Your Android app should now run locally on your emulator or connected device.

 <h2 id="developer-instruction">Developer Instruction</h2>

##### Add your Server url to the all save data and get data classes for storing indoor data and get data while navigation.

```java

private static final String CREATE_URL = "Add_Your_Server_Url";

```

##### Create HTTP connection

```java


URL url = new URL(CREATE_URL);

HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
urlConnection.setRequestMethod("POST");
urlConnection.setRequestProperty("Content-Type", "application/json");
urlConnection.setDoOutput(true);

```

##### After store data/get data disconnect HTTP connection

```java

 urlConnection.disconnect();

```

#### Deploying the PHP files in Remote Server.

- Step 1: There are code files for the PHP REST API. Deploy these files on your remote server.

- Step 2: In the PHP_REST_APP, be sure to replace 'your_database_name,' 'your_username,' and 'your_password' with your actual database connection details in the `/php_rest_app/config/Database.php` file.

```php
// Replace these values with your database credentials
private $host = 'localhost';
private $db_name = 'your_database_name';
private $username = 'your_username';
private $password = 'your_password';
private $conn;
```

- Step 3: Test your API by visiting your server's IP address or domain name and appending the path to your PHP API endpoint in a web browser. You can also use tools like curl or Postman for testing.

```java

http://<server_ip>/php_rest_app/api/nodeinfo/read.php

```

## <h2 id="find-bug">Find Bug?</h2>

If you have found an issue or would like to submit an improvement to this project, please submit an issue using the 'Issues' tab above. If you would like to submit a pull request (PR) with a fix, please reference the issue you created

## <h2 id="issues">Know issues (Work In Progress)</h2>

- User interface for a background blueprint
- Efficient Searching Navigation.
- Improving live tracking when users navigate the app.

## Contributor

- <a href= "https://github.com/frijve99"> Firoz Mahmud Rijve </a> <br>
- <a href= "https://github.com/sunnysakib">Sakibur Rahaman</a> <br>
- <a href= "https://github.com/KaziSifatAlMaksud"> Kazi Sifat Al Maksud </a>
- <a href= "https://github.com/Anik228"> Anik Lal Day</a>

## License

Published and distributed under the MIT License. See LICENSE for more information.

 <h2 id="support">Support - Like This Project ?</h2>

Stay tuned for exciting updates and progress on our project! Follow us on GitHub for the latest news and developments. Your support inspires us.


## Contract

<h4>Email</h4>

- kazi.sifat2013@gmail.com
- aniklal2020@gmail.com
- frijve99@gmail.com
- sakibur.rahaman033@gmail.com




