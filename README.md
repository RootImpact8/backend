# <div align="center"> **[🏆대상 수상작] AI 영농일지 플랫폼 : 싹 SAC :seedling:**</div>
![Image](https://github.com/user-attachments/assets/f1aca800-a39f-4afe-9263-6438b5264541)
<br/>
<br/>
### <div align="center">🌱싹 틔우는 순간부터 🚚출하까지 싹으로 관리하세요.</div>
<div align="center">📒영농일지 싹은 AI가 ☀️날씨와 📆작물 재배 일정을 고려해 그 날의 작업을 추천하고,</div>
<div align="center">✏️내가 입력한 활동을 바탕으로 🍎예상 수확 시기를 제공하는 어플입니다.</div>
<br/>
<br/>

## 목차
1. [프로젝트 개요](#1)
2. [기술 스텍](#2)
3. [주요 기능](#3)
4. [상세 기능](#4)
5. [운해 팀원 소개](#5)

<br/>

## <span id ="1"> 프로젝트 개요 </span>

### 프로젝트 명
> <img src ="https://github.com/user-attachments/assets/a2247d3e-3007-4ff4-90d4-c601d452a6cf" width="30" height="30"> 싹 (**S**mart **A**griculture **C**alendar) <br>

### 개발 기간 
> 2025.01.15(Wed) ~ 2025.02.14(Fri)

### 개발 의도
> 지역의 농민들은 기상 이변에 따른 작물 수확량과 품질 감소를 겪고 있습니다. <br>
당장 지구 온난화에 따른 이상기후를 막을 수 없을지라도 적절한 조치를 통해 작물 피해를 최소화할 수 있습니다.<br>
이번 해커톤을 통해 이상기후에 따른 대비책을 제공하는 AI모델을 제작, 일상에 쉽게 적용할 수 있는 솔루션을 제작했습니다.<br>

### 루트임팩트 AI 커리어 스쿨 해커톤 🥇대상 수상작  
> **주최** 루트임팩트 **운영** 멋쟁이사자처럼 **후원** 구글닷오알지 (총 상금 2000만원 규모) <br>
> [해커톤 성료 링크](https://www.edaily.co.kr/News/Read?newsId=03529286642072160&mediaCodeNo=257)

<br/>

## <span id ="2"> Stacks </span>
<div align="center"> <img src="https://img.shields.io/badge/react-61DAFB?style=for-the-badge&logo=react&logoColor=white"> <img src="https://img.shields.io/badge/cssmodules-000000?style=for-the-badge&logo=cssmodules&logoColor=white"> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/amazonrds-FF9900?style=for-the-badge&logo=amazonrds&logoColor=white"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/geeksforgeeks-2F8D46?style=for-the-badge&logo=geeksforgeeks&logoColor=white"> </div>

![Image](https://github.com/user-attachments/assets/491b54b0-c42c-42c1-b437-88b675ad1189)


<br/>

## <span id ="3"> 주요 기능 </span>
|<img src="https://github.com/user-attachments/assets/dea08b4c-42cc-4cf5-8ce6-efdcef92632e" width="480" height="270"/>|<img src="https://github.com/user-attachments/assets/a473dc53-22a1-4734-ad24-bb1a8206067c" width="480" height="270"/>|
|:-:|:-:|
|**🤖 재배 활동 추천 AI**|**📝 영농일지 작성**|

[서비스 기획 PT 자료](https://deep-rook-d58.notion.site/AI-PT-19da6bf9de47806ab83cd22c29b11e01?pvs=4)

<br/>

## <span id ="4"> 구현 기능 </span>

### 🏠 Home
|**기능**|<div align="center">**설명**</div>|
|:-:|:-| 
|내 위치 저장|주소를 불러오거나 시/군/구를 직접 설정할 수 있습니다.|
|기상 예보|위치로 설정한 지역의 기온, 강수량, 풍속 및 기상 특보를 제공합니다.|
|관심 작물 동향|재배 중인 작물 외에도 등록한 관심 작물에 관한 최근 뉴스를 정리해 제공합니다.|
|추천 활동|등록한 재배 주기를 바탕으로 준비, 생육, 수확 작업을 추천합니다.|
|활동 TIP|날이 맑아 재배 활동이 가능하면 추천하는 작업 세부 사항을 제공합니다.|
|기상이변 대비|기상 주의보, 경보가 내려지면 농지 정비 및 작물 처치와 관련한 세부 수칙을 제공합니다.|
|현재 작물 도매가|몇 달 간 가락시장에서 거래된 작물 경매가를 그래프로 표시합니다.|

<br/>

### 📔 Diary
|**기능**|<div align="center">**설명**</div>|
|:-:|:-| 
|작업 달력|내가 등록한 일지를 달 별로 모아볼 수 있습니다.|
|작물 타임라인|개별 작물의 작업 일지를 모아볼 수 있습니다.|
|일지 작성|오늘 날짜를 기본으로 해당하는 날씨가 자동으로 기입됩니다.|
|작물 선택|일지 작성 시 재배 중인 작물 중 선택할 수 있습니다.|
|활동 선택|개별 작물의 주기 별 활동을 불러와 선택할 수 있습니다.|
|사진 저장|작업 내용이 담긴 사진을 일지에 저장할 수 있습니다.|

<br/>

### 👨‍🌾 My page
|**기능**|<div align="center">**설명**</div>|
|:-:|:-| 
|로그인|이메일과 비밀번호를 등록하고 내 일지를 관리할 수 있습니다.|
|재배 작물 설정|일지를 작성할 작물을 등록할 수 있습니다.|
|관심 작물 설정|재배 중인 작물 외에도 뉴스를 받아볼 작물을 등록할 수 있습니다.|

<br/>

## <span id ="5"> ⛅️ 운해 팀원 소개 </span>
|<img src="https://avatars.githubusercontent.com/pppineappple" width="130" height="130"/>|<img src="https://avatars.githubusercontent.com/OckJuYong" width="130" height="130"/>|<img src="https://avatars.githubusercontent.com/SemiHyeon" width="130" height="130"/>|<img src="https://avatars.githubusercontent.com/maeng555" width="130" height="130"/>|<img src="https://avatars.githubusercontent.com/h0725j" width="130" height="130"/>|<img src="" width="130" height="130"/>|
|:-:|:-:|:-:|:-:|:-:|:-:|
|[**PM 신승아**](https://github.com/pppineappple)|[**FE 옥주용**](https://github.com/OckJuYong)|[**FE 현세미**](https://github.com/SemiHyeon)|[**BE 맹진영**](https://github.com/maeng555)|[**BE 최현지**](https://github.com/h0725j)|[**DE 박재원**]()|



---

<br/>

🍀**영농일지 싹과 함께 마음 속에 심어둔 귀촌의 싹을 틔워보세요.**

<!--
**Here are some ideas to get you started:**

🙋‍♀️ A short introduction - what is your organization all about?
🌈 Contribution guidelines - how can the community get involved?
👩‍💻 Useful resources - where can the community find your docs? Is there anything else the community should know?
🍿 Fun facts - what does your team eat for breakfast?
🧙 Remember, you can do mighty things with the power of [Markdown](https://docs.github.com/github/writing-on-github/getting-started-with-writing-and-formatting-on-github/basic-writing-and-formatting-syntax)
-->
