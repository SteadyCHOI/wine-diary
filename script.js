// 카카오 SDK 초기화 (실제 앱키로 교체 필요)
Kakao.init('6ad44bad8196bf623d3107f4a0f3e5cf'); // 실제 카카오 앱키로 교체하세요

// 사용자 상태 관리
let currentUser = null;

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    initializeGoogleSignIn();
    checkLoginStatus();
});

// 구글 로그인 초기화
function initializeGoogleSignIn() {
    google.accounts.id.initialize({
        client_id: 'YOUR_GOOGLE_CLIENT_ID', // 실제 구글 클라이언트 ID로 교체하세요
        callback: handleGoogleSignIn
    });

    google.accounts.id.renderButton(
        document.getElementById('google-signin-btn'),
        {
            theme: 'outline',
            size: 'large',
            text: 'signin_with',
            locale: 'ko'
        }
    );
}

// 카카오 로그인
function loginWithKakao() {
    Kakao.Auth.login({
        success: function(response) {
            console.log('카카오 로그인 성공:', response);
            getKakaoUserInfo();
        },
        fail: function(error) {
            console.error('카카오 로그인 실패:', error);
            alert('카카오 로그인에 실패했습니다.');
        }
    });
}

// 카카오 사용자 정보 가져오기
function getKakaoUserInfo() {
    Kakao.API.request({
        url: '/v2/user/me',
        success: function(response) {
            const user = {
                id: response.id,
                name: response.properties.nickname,
                provider: 'kakao'
            };
            setUser(user);
        },
        fail: function(error) {
            console.error('사용자 정보 가져오기 실패:', error);
        }
    });
}

// 구글 로그인 처리
function handleGoogleSignIn(response) {
    try {
        const payload = parseJwt(response.credential);
        const user = {
            id: payload.sub,
            name: payload.name,
            email: payload.email,
            provider: 'google'
        };
        setUser(user);
    } catch (error) {
        console.error('구글 로그인 처리 실패:', error);
        alert('구글 로그인에 실패했습니다.');
    }
}

// JWT 토큰 파싱
function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    return JSON.parse(jsonPayload);
}

// 사용자 설정
function setUser(user) {
    currentUser = user;
    localStorage.setItem('wine_diary_user', JSON.stringify(user));
    updateUI();
}

// 로그아웃
function logout() {
    if (currentUser && currentUser.provider === 'kakao') {
        Kakao.Auth.logout();
    }

    currentUser = null;
    localStorage.removeItem('wine_diary_user');
    updateUI();
}

// 로그인 상태 확인
function checkLoginStatus() {
    const savedUser = localStorage.getItem('wine_diary_user');
    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        updateUI();
    }
}

// UI 업데이트
function updateUI() {
    const authButtons = document.getElementById('auth-buttons');
    const userInfo = document.getElementById('user-info');
    const userName = document.getElementById('user-name');
    const loggedInActions = document.getElementById('logged-in-actions');
    const loginPrompt = document.getElementById('login-prompt');

    if (currentUser) {
        authButtons.style.display = 'none';
        userInfo.classList.remove('hidden');
        userName.textContent = `${currentUser.name}님`;
        loggedInActions.style.display = 'block';
        loginPrompt.style.display = 'none';
    } else {
        authButtons.style.display = 'flex';
        userInfo.classList.add('hidden');
        loggedInActions.style.display = 'none';
        loginPrompt.style.display = 'block';
    }
}

// 테이스팅 노트 작성 페이지로 이동
function goToWritePage() {
    if (!currentUser) {
        alert('로그인이 필요합니다.');
        return;
    }
    window.location.href = 'write.html';
}