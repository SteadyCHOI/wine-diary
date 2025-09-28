// 카카오 SDK 초기화
if (typeof Kakao !== 'undefined' && !Kakao.isInitialized()) {
    Kakao.init('YOUR_KAKAO_APP_KEY');
}

// 전역 변수
let currentUser = null;

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    //checkLoginStatus();
    loadRecentActivities();
});

// 로그인 상태 확인
function checkLoginStatus() {
    const savedUser = localStorage.getItem('wine_diary_user');
    const token = localStorage.getItem('wine_diary_token');

    if (!savedUser || !token) {
        alert('로그인이 필요합니다.');
        window.location.href = 'index.html';
        return;
    }

    currentUser = JSON.parse(savedUser);
    document.getElementById('user-name').textContent = `${currentUser.name}님`;
}

// 최근 활동 로드
function loadRecentActivities() {
    const activities = [];

    // 로컬 스토리지에서 최근 노트들 가져오기
    const savedNotes = JSON.parse(localStorage.getItem('wine_diary_notes') || '[]');

    // 최근 5개 노트만 표시
    const recentNotes = savedNotes
        .sort((a, b) => new Date(b.date) - new Date(a.date))
        .slice(0, 5);

    recentNotes.forEach(note => {
        activities.push({
            type: 'note',
            title: `${note.wine.name} 테이스팅 완료`,
            description: `평점: ${'★'.repeat(note.rating)}${'☆'.repeat(5 - note.rating)}`,
            date: new Date(note.date).toLocaleDateString('ko-KR'),
            icon: 'fas fa-wine-glass'
        });
    });

    // 활동이 없는 경우
    if (activities.length === 0) {
        activities.push({
            type: 'empty',
            title: '아직 활동이 없습니다',
            description: '첫 번째 테이스팅 노트를 작성해보세요!',
            date: '',
            icon: 'fas fa-info-circle'
        });
    }

    displayActivities(activities);
}

// 활동 표시
function displayActivities(activities) {
    const container = document.getElementById('recent-activities');

    container.innerHTML = activities.map(activity => `
        <div class="activity-item">
            <div class="activity-icon">
                <i class="${activity.icon}"></i>
            </div>
            <div class="activity-content">
                <h4>${activity.title}</h4>
                <p>${activity.description}</p>
            </div>
            <div class="activity-date">${activity.date}</div>
        </div>
    `).join('');
}

// 준비 중 기능 알림
function showComingSoon() {
    alert('이 기능은 곧 업데이트될 예정입니다!');
}

// 로그아웃
function logout() {
    if (currentUser && currentUser.provider === 'kakao') {
        Kakao.Auth.logout();
    }

    localStorage.removeItem('wine_diary_user');
    localStorage.removeItem('wine_diary_token');
    alert('로그아웃되었습니다.');
    window.location.href = 'index.html';
}