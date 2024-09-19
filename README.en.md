# Menteetor

- **Project Duration**: Jan 2024 ~ Jun 2024
- **Project Members**: 4
  - **Back-End**: 2 developers
  - **Front-End**:
    - Android: 1 developer
    - Web: 1 developer

---

## Project Purpose

> During university, it was often difficult to find someone to help with academic questions or uncertainties.  
> I thought that finding a mentor within the same college could be very helpful for university life.  
> Currently, there are community apps for campus communication, but they are limited in providing professional academic information.  
> We felt the need for a specialized community where users could quickly and easily post questions to get help.  
> We also wanted to create a platform where seniors or graduates could share their knowledge with juniors.  
> Furthermore, we believed it would be beneficial for growth if students could interact with others from different universities with the same major. This led to the planning of this project.

---

## Project Features

- Users can use the service for any school or their own school.
  
- Users who want to become mentors can write a post about the subject and conditions for mentee recruitment.

- Users can fill out an application form for mentee recruitment, and the mentor decides whether to match based on the application.

- If a match is made, real-time consultation between the mentor and mentee can continue through real-time chat.

- Users can post questions on the Q&A board and communicate through comments.

- Users can fill out the application form for mentee recruitment.

- Mentors can review the details of the application and decide whether to match.

- Once a match is made, real-time consultation between the mentor and mentee can continue through real-time chat.

- After a match is made, mentees can request an extension of the consultation time during the chat.

- All users can post questions on the Q&A board and communicate through comments.

- All users can post suggestions for mentor recruitment on the mentor request board and communicate through comments.

---

## ERD

![image](https://github.com/FiveIdiotss/back-end-api/assets/109346159/9e7d163e-0049-48c0-8a85-462c0f12d3d3)

---

## Architecture

![image](https://github.com/FiveIdiotss/back-end-api/assets/109346159/9f4b2ad7-9d56-4a39-9a28-58a3d275d003)

---

## Technology Stack
**Back-End**: <br> 
- <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
- <img src="https://img.shields.io/badge/spring%20security-6DB33F?style=for-the-badge&logo=spring%20security&logoColor=white">
- <img src="https://img.shields.io/badge/json%20web%20tokens-000000?style=for-the-badge&logo=json%20web%20tokens&logoColor=white">
- <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
- <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
- <img src="https://img.shields.io/badge/amazon%20ec2-FF9900?style=for-the-badge&logo=amazon%20ec2&logoColor=white">
- <img src="https://img.shields.io/badge/amazon%20rds-527FFF?style=for-the-badge&logo=amazon%20rds&logoColor=white">
- <img src="https://img.shields.io/badge/amazon%20route%2053-8C4FFF?style=for-the-badge&logo=amazon%20route%2053&logoColor=white">
- <img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white">

**Front-End Web**:
- <img src="https://img.shields.io/badge/next.js-000000?style=for-the-badge&logo=next.js&logoColor=white">
- <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white">
- <img src="https://img.shields.io/badge/react%20query-FF4154?style=for-the-badge&logo=react%20query&logoColor=white">
- <img src="https://img.shields.io/badge/auth.js-EB5424?style=for-the-badge&logo=auth0&logoColor=white">
- <img src="https://img.shields.io/badge/tailwind%20css-06B6D4?style=for-the-badge&logo=tailwind%20css&logoColor=white">
- <img src="https://img.shields.io/badge/zustand-000000?style=for-the-badge&logo=next&logoColor=white">

**Front-End Mobile**:
- <img src="https://img.shields.io/badge/databinding-000000?style=for-the-badge&logo=next&logoColor=white">
- <img src="https://img.shields.io/badge/coroutine-000000?style=for-the-badge&logo=jetpack&logoColor=white">
- <img src="https://img.shields.io/badge/flow-000000?style=for-the-badge&logo=jetpack&logoColor=white">

**Tools**:
- <img src="https://img.shields.io/badge/android%20studio-3DDC84?style=for-the-badge&logo=android%20studio&logoColor=white">
- <img src="https://img.shields.io/badge/visual%20studio%20code%20studio-007ACC?style=for-the-badge&logo=visual%20studio%20code&logoColor=white">
- <img src="https://img.shields.io/badge/intellij%20idea-000000?style=for-the-badge&logo=intellij%20idea&logoColor=white">

**Collaboration**:
- <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">

**etc:**
- <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
- <img src="https://img.shields.io/badge/firebase%20cloud%20messaging-FFCA28?style=for-the-badge&logo=firebase&logoColor=white">
- <img src="https://img.shields.io/badge/amazon%20s3-569A31?style=for-the-badge&logo=amazon%20s3&logoColor=white">
- <img src="https://img.shields.io/badge/web%20socket-000?style=for-the-badge&logo=alogoColor=white">

---

## Web Screen Flowchart

To be added

---

## Mobile Screen Flowchart

![img.png](img.png)

---

## Detailed Features

- 🔐 **Account**

  - Admin Account
  - Registration
  - University Authentication
  - Login/Logout
  - Social Login (Naver, Kakao, Google)
  - Validation/Token
  - Auto Login
  - Password Change

- 🏡 **Main Board**

  - Write Mentee recruitment post according to the template
  - Apply after viewing posts
  - Favorites
  - View board list by filter (search, favorites, date, category, school)
  - View representative images for each post in the board list
  - Infinite Scroll
  - Provide popular keywords

- 👤 **Profile**

  - Manage profile pictures
  - Manage/Edit personal information
  - List of posts created by the user
  - Application/Matching List
  - Notification list

- 💬 **Chat Room**

  - Real-time chat when mentor-mentee matching occurs
  - Chat room list
  - Real-time count of unread messages for each chat room / reset upon viewing
  - Mark messages as read
  - Send images, files, etc., besides text
  - Request for chat time extension (no duplicate extension requests)
  - Accept/Reject chat time extension requests

- 🏡 **Q&A Board**

  - Write a question post
  - Likes
  - Comments
  - View list by board filter
  - Infinite Scroll

- 🏡 **Mentoring Request Board**

  - Write a request post
  - Likes
  - Comments
  - View list by board filter
  - Infinite Scroll

- 🔔 **Notifications**

  - Notifications about chat, application, etc.
  - Real-time notification count / reset upon viewing

- 💸 **Payment (planned)**
  - Coin recharge
  - Coin exchange
  - Coin withdrawal

---

## Development Process

- To handle response values for each API, created a `CommonAPIResponse` class to send the success status, error message on failure, and response value on success to the FE.

- To mitigate the risk in case the accessToken is not expired and is stolen even after the user logs out, adopted a method of saving the accessToken in the database by adding a `BlackList` entity.

- Initially attempted to implement the notification service using SSE. Although SSE allows real-time data transmission from the server to the client over HTTP without a separate protocol and automatically reconnects when the network connection is lost, it was challenging for the server to detect when the client closes the page (background state). Therefore, FCM was chosen.

- To implement the "read" feature for chats, it was necessary to identify how many users were currently connected to a specific chat room in real-time. Determined that using an Interceptor that runs whenever a specific user joins (subscribes) to the chat room was appropriate. By reading the chat room ID and user ID from the STOMP header via Interceptor, we could update the connected user information in Redis in real time.

- Initially used a method to serialize image files as Base64 and send them through a WebSocket for image transmission in the chat room. However, this method had issues such as the maximum file size limit when sending files over WebSocket, the complexity of the serialization process, and the unsuitability for sending various types of files (videos, contacts, etc.). For these reasons, adopted the method of using the HTTP protocol's multipart/form-data format for transmission (messages are sent via WebSocket, while other files are sent via the HTTP protocol).

- Considered creating a separate AWS EC2 for the Front End's next.js but decided to use a single EC2 instance to deploy both Back and Front using Docker containers for cost efficiency.

- Due to the overuse of try-catch blocks and poorly organized exception handling, readability was low. Used the `@RestControllerAdvice` annotation to handle exceptions more effectively.

---

## Improvements

- Currently, Redis is primarily used for real-time chat using the pub/sub feature. The project's service data is all stored in the main DB, which is not causing any significant issues at the moment due to the small scale of the service. However, caching information like chat history, FCM tokens, and RefreshToken in Redis to reduce the load on the main DB should be considered for future development.

- Currently running Spring Boot on AWS EC2 with a WAS, but to improve security and performance (load balancing, etc.), introducing Nginx should be considered.
