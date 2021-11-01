# DigitalRefrige
COMP90018



<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a>
    <img src="https://github.com/pikachuabc/DigitalRefrige/blob/master/app/src/main/res/mipmap-hdpi/ic_egg.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Digital Refrige</h3>

  <p align="center">
    An awesome digital refrige recorder!
    <br />
    <a href="hhttps://github.com/pikachuabc/DigitalRefrige/edit/master/README.md"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/pikachuabc/DigitalRefrige">View Demo</a>
    ·
    <a href="https://github.com/pikachuabc/DigitalRefrige/issues">Report Bug</a>
    
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project
<p align="center">

<img width="150" alt="Screen Shot 2021-10-31 at 2 31 20 am" src="https://user-images.githubusercontent.com/61958672/139539629-72a2f88b-20bf-4d8f-a876-96f401c75caa.png">
<img width="150" alt="Screen Shot 2021-10-31 at 2 31 37 am" src="https://user-images.githubusercontent.com/61958672/139539635-991c22d5-0d76-42f9-a300-b5b98dedf9d9.png">
<img width="150" alt="Screen Shot 2021-10-31 at 2 31 49 am" src="https://user-images.githubusercontent.com/61958672/139539617-247b68f5-9ed2-4c2c-a1b3-a1a4839a32fa.png">
</p>



It is an Android app based on the function of reminding the expiration date of food in the fridge or locker, which often leads to the waste of food. This app records the expiration date of food purchased by users and reminds them regularly to reduce the waste of food.

<p align="right">(<a href="#top">back to top</a>)</p>



### Built With

This section should list any major frameworks/libraries used to bootstrap your project. Leave any add-ons/plugins for the acknowledgements section. Here are a few examples.



* [Android studio](https://developer.android.com/)
<p align="right">(<a href="#top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

### Prerequisites

This is an example of how to list things you need to use the software and how to install them.
* Andriod studio virtual device
  ```
  AVD Manager -> Create Virtual Device -> Pixel2/Pixel4 -> API 28(Pie)
  ```
* Real Andriod device wiht above API
* Google SignIn
  In the project directory, using the Gradle signingReport command to get SHA-1 (making sure your java is above version 11): 
  ```
  ./gradlew signingReport
  ```
  
  The signing report will include the signing information for each of your app's variants:
  ```
    > Task :app:signingReport
  Variant: debug
  Config: debug
  Store: ~/.android/debug.keystore
  Alias: AndroidDebugKey
  MD5: A5:88:41:04:8D:06:71:6D:FE:33:76:87:AC:AD:19:23
  SHA1: A7:89:E5:05:C8:17:A1:22:EA:90:6E:A6:EA:A3:D4:8B:3A:30:AB:18
  SHA-256: 05:A2:2C:35:EE:F2:51:23:72:4D:72:67:A5:6C:8C:58:22:2A:00:D6:DB:F6:45:D5:C1:82:D2:80:A4:69:A8:FE
  Valid until: Wednesday, August 10, 2044
  ```
  Please email yueruc@student.unimelb.edu.au about your SHA-1 and then you will be able to login with your google account.
  

### Installation

_Below is an example of how you can instruct your audience on installing and setting up your app. This template doesn't rely on any external dependencies or services._

1. Clone the repo
   ```sh
   git clone https://github.com/pikachuabc/DigitalRefrige.git
   ```
2. Install on Virtual device or Real device
3. Run the application on phone

<p align="right">(<a href="#top">back to top</a>)</p>






<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=for-the-badge
[contributors-url]: https://github.com/othneildrew/Best-README-Template/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/othneildrew/Best-README-Template.svg?style=for-the-badge
[forks-url]: https://github.com/othneildrew/Best-README-Template/network/members
[stars-shield]: https://img.shields.io/github/stars/othneildrew/Best-README-Template.svg?style=for-the-badge
[stars-url]: https://github.com/othneildrew/Best-README-Template/stargazers
[issues-shield]: https://img.shields.io/github/issues/othneildrew/Best-README-Template.svg?style=for-the-badge
[issues-url]: https://github.com/othneildrew/Best-README-Template/issues
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/othneildrew
[product-screenshot]: images/screenshot.png
