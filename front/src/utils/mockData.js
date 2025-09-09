const sampleTexts = [
  "오늘 날씨가 정말 좋네요! 🌞",
  "새로운 프로젝트를 시작했습니다. React와 Node.js로 개발 중이에요.",
  "커피 한 잔의 여유 ☕️ #일상 #커피",
  "주말에 산책하기 좋은 날씨입니다. 🚶‍♂️",
  "오늘 읽은 책이 너무 재미있었어요. 추천합니다! 📚",
  "새로운 기술을 배우는 것은 항상 즐거워요. #개발 #학습",
  "점심으로 맛있는 파스타를 먹었습니다. 🍝",
  "운동 후 기분이 정말 좋아요! 💪",
  "친구들과 즐거운 시간을 보냈습니다. 😊",
  "새로운 영화를 봤는데 정말 감동적이었어요. 🎬",
  "집에서 요리하는 재미에 빠져있어요. 👨‍🍳",
  "음악 들으면서 코딩하는 시간 🎵",
  "오늘도 열심히 일했습니다. 수고하셨어요! 💼",
  "주말 계획을 세우고 있어요. 뭘 할까요? 🤔",
  "새로운 카페를 발견했어요. 분위기가 정말 좋아요! ☕️",
  "운동을 꾸준히 하니까 체력이 늘어나는 게 느껴져요. 🏃‍♀️",
  "독서의 즐거움을 다시 발견했습니다. 📖",
  "가족과 함께 보내는 시간이 가장 소중해요. 👨‍👩‍👧‍👦",
  "새로운 취미를 시작해볼까 생각 중이에요. 🎨",
  "오늘 하루도 감사한 마음으로 마무리합니다. 🙏"
];

const sampleUsers = [
  { name: "김철수", username: "kimcs", avatar: "https://picsum.photos/40/40?random=1" },
  { name: "박영희", username: "parkyh", avatar: "https://picsum.photos/40/40?random=2" },
  { name: "이민수", username: "leems", avatar: "https://picsum.photos/40/40?random=3" },
  { name: "최수진", username: "choisj", avatar: "https://picsum.photos/40/40?random=4" },
  { name: "정동현", username: "jeongdh", avatar: "https://picsum.photos/40/40?random=5" },
  { name: "한지민", username: "hanjm", avatar: "https://picsum.photos/40/40?random=6" },
  { name: "윤서준", username: "yunsj", avatar: "https://picsum.photos/40/40?random=7" },
  { name: "강혜진", username: "kanghj", avatar: "https://picsum.photos/40/40?random=8" },
  { name: "조민우", username: "jomw", avatar: "https://picsum.photos/40/40?random=9" },
  { name: "송은지", username: "songej", avatar: "https://picsum.photos/40/40?random=10" }
];

const getRandomTimeAgo = () => {
  const timeOptions = [
    "방금 전",
    "1분 전",
    "5분 전",
    "10분 전",
    "30분 전",
    "1시간 전",
    "2시간 전",
    "3시간 전",
    "5시간 전",
    "1일 전",
    "2일 전",
    "3일 전"
  ];
  return timeOptions[Math.floor(Math.random() * timeOptions.length)];
};

const getRandomStats = () => ({
  replies: Math.floor(Math.random() * 50),
  retweets: Math.floor(Math.random() * 100),
  likes: Math.floor(Math.random() * 200),
  views: Math.floor(Math.random() * 1000) + 100
});

const getRandomImages = () => {
  const shouldHaveImages = Math.random() > 0.7; // 30% 확률로 이미지 있음
  if (!shouldHaveImages) return [];
  
  const imageCount = Math.floor(Math.random() * 10) + 1; // 1~10장
  const images = [];
  
  for (let i = 0; i < imageCount; i++) {
    images.push(`https://picsum.photos/500/300?random=${Date.now()}_${i}`);
  }
  
  return images;
};

let tweetIdCounter = 1;

export const generateMockTweets = (count = 10, specificUser = null) => {
  const tweets = [];
  
  for (let i = 0; i < count; i++) {
    const user = specificUser || sampleUsers[Math.floor(Math.random() * sampleUsers.length)];
    const text = sampleTexts[Math.floor(Math.random() * sampleTexts.length)];
    const stats = getRandomStats();
    
    const images = getRandomImages();
    
    tweets.push({
      id: tweetIdCounter++,
      user,
      text,
      time: getRandomTimeAgo(),
      ...stats,
      isLiked: Math.random() > 0.7,
      isRetweeted: Math.random() > 0.9,
      images: images,
      imageUrl: images.length > 0 ? images[0] : null // 호환성을 위해 첫 번째 이미지 유지
    });
  }
  
  return tweets;
};

export const getUserByUsername = (username) => {
  return sampleUsers.find(user => user.username === username) || {
    name: username,
    username: username,
    avatar: `https://picsum.photos/40/40?random=${username}`
  };
};

export const generateUserSpecificTweets = (username, count = 10) => {
  const user = getUserByUsername(username);
  return generateMockTweets(count, user);
};

export const simulateApiDelay = (ms = 1000) => {
  return new Promise(resolve => setTimeout(resolve, ms));
};