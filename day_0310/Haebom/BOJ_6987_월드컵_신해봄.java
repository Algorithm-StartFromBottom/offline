import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class BOJ_6987_월드컵_신해봄 {
	
    static int[][] result;
    static boolean isPossible;
    static int[] team1;
    static int[] team2;
    static int[][] matchResult;
    
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        
        // 모든 가능한 경기 조합 미리 계산 (15개의 경기)
        team1 = new int[15];
        team2 = new int[15];
        int index = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = i + 1; j < 6; j++) {
                team1[index] = i;
                team2[index] = j;
                index++;
            }
        }
        
        for (int tc = 1; tc <= 4; tc++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            
            result = new int[6][3]; // [팀][승, 무, 패]
            
            for (int i = 0; i < 6; i++) {
                result[i][0] = Integer.parseInt(st.nextToken()); // 승
                result[i][1] = Integer.parseInt(st.nextToken()); // 무
                result[i][2] = Integer.parseInt(st.nextToken()); // 패
            }
            
            matchResult = new int[6][3];
            
            int totalWins = 0;
            int totalDraws = 0;
            int totalLoses = 0;
            
            for (int i = 0; i < 6; i++) {
                totalWins += result[i][0];
                totalDraws += result[i][1];
                totalLoses += result[i][2];
            }
            
            // 승 = 패, 무승부는 짝수여야 함, 총 경기 수는 15*2=30이어야 함
            isPossible = false;
            if (totalWins == totalLoses && totalDraws % 2 == 0 && totalWins + totalDraws + totalLoses == 30) {
                backtracking(0);
            }
            
            sb.append("#").append(tc).append(" ").append(isPossible ? 1 : 0).append("\n");
        }
        
        System.out.print(sb);
    }
    
    // 백트래킹으로 가능한 경기 결과 확인
    // matchCount: 현재까지 진행한 경기 수
    static void backtracking(int matchCount) {
        // 이미 가능한 경우를 찾았다면 더 이상 탐색하지 않음
        if (isPossible) {
            return;
        }
        
        // 모든 경기(15경기)를 다 진행한 경우
        if (matchCount == 15) {
            isPossible = true;
            return;
        }
        
        // 현재 경기에 참여하는 두 팀
        int t1 = team1[matchCount];
        int t2 = team2[matchCount];
        
        // 경우 1: 팀1 승리, 팀2 패배
        if (matchResult[t1][0] < result[t1][0] && matchResult[t2][2] < result[t2][2]) {
            matchResult[t1][0]++;
            matchResult[t2][2]++;
            backtracking(matchCount + 1);
            matchResult[t1][0]--;
            matchResult[t2][2]--;
        }
        
        // 경우 2: 무승부
        if (matchResult[t1][1] < result[t1][1] && matchResult[t2][1] < result[t2][1]) {
            matchResult[t1][1]++;
            matchResult[t2][1]++;
            backtracking(matchCount + 1);
            matchResult[t1][1]--;
            matchResult[t2][1]--;
        }
        
        // 경우 3: 팀1 패배, 팀2 승리
        if (matchResult[t1][2] < result[t1][2] && matchResult[t2][0] < result[t2][0]) {
            matchResult[t1][2]++;
            matchResult[t2][0]++;
            backtracking(matchCount + 1);
            matchResult[t1][2]--;
            matchResult[t2][0]--;
        }
    }
}