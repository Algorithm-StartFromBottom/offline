package a250307.boj17135castleDefense;

import java.io.*;
/*
 * 맵의 길이 범위가 3<= N, M <= 15 이므로
 * 적이 한칸씩 이동할때와 같은 연산에서
 * 비트마스크를 쓰는 것이 가장 빠를 것 같습니다.
 * 
 * 비트 마스크를 사용하므로 다음과 같이 생각하겠습니다.
 * 
 * 적은 좌측에서 우측으로 한칸씩 이동.
 * 궁수들은 최우측에 배치.
 * 
 * 매 턴마다 궁수들이 죽일 적을 계산하여
 * 한번에 맵에 반영하고,
 * 
 * 적을 한칸씩 옮기는 일을 반복합니다.
 * 
 * 적이 맵에 없으면 해당 궁수 배치에 대한 kill 결과값을 확인하여
 * 최대 kill 값을 업데이트 합니다.
 * 
 * 
 * 
 * 
 */
import java.util.Arrays;

public class BOJ_17135_캐슬디펜스_송석현 {

    static BufferedReader br;
    static StreamTokenizer st;

    // N은 맵의 길이
    // M은 라인의 갯수 (궁수들이 위치할 수 있는 포지션의 갯수)
    // D는 Distance
    static int N, M, D;

    // receivedMap은 처음 입력시의 값을 보존
    // bitmaskMap은 플레이시 사용할 것.
    static int[] bitmaskMap;
    static int[] receivedMap;

    // archerSet은 궁수들의 위치를 담은 배열입니다.
    static int maxKill;
    static int[] archerSet;

    static int[][] attackRange;
    
    static int enemyNumber;
    static int remainder;
    static int estimatedPossibleMaxKillNum;




    /*
     * 전체 적 세는 로직과
     * 남아있는 적을 세는 로직을 추가하여 프루닝 했지만
     * 채점시 시간이 저혀 줄어들지 않네요..
     */
    public static void main(String[] args) throws IOException {
        init();
        do {
            //비트마스크 맵 초기화
            bitmaskMap = receivedMap.clone();
            //플레이하여 맥스 킬수 업데이트
            playOneArcherSet();

            if(maxKill == estimatedPossibleMaxKillNum) break;
            //궁수위치 이터레이션
        } while(setNextArcherPosition());
        System.out.println(maxKill);
    }

    // 궁수들 턴 -> 적의 이동 턴
    // 반복하면서 매 턴마다 킬수를 누적하여 저장합니다.
    static void playOneArcherSet() {
        setAttackRange();
        int killCount=0;
        do {
            killCount += archerTurn();
            enemyTurn();
        }
        while(remainder !=0 && killCount+remainder > maxKill);

        if(killCount > maxKill) maxKill=killCount;
    }


    // 궁수들이 죽일 적들을 결정하고
    // 맵에 적의 죽음을 반영, 죽은 적의 수를 리턴합니다.
    static int archerTurn() {
        int[] killPoints = new int[3];
        Arrays.fill(killPoints, -1);
        //궁수들의 위치를 순회하며 궁수가 죽일 적의 좌표를 찾습니다.
        for(int archer=0; archer<3; archer++) {
            for(int encodedRC : attackRange[archer]) {
                if(isEnemyAt(encodedRC)) {
                    killPoints[archer] = encodedRC;
                    break;
                }
            }
        }
        // 비트 마스크에 적의 죽음을 반영
        int killCount=0;
        for(int encodedRC : killPoints) {
            if(encodedRC >= 0 && isEnemyAt(encodedRC)) {
                setKillAt(encodedRC);
                killCount++;
            }
        }
        return killCount;
    }


    //궁수가 죽일 수 잇는 적의 범위 배열을 초기화해줍니다.
    static void setAttackRange() {
        ///0번부터 2번까지 궁수 순회
        for(int archer=0; archer<3; archer++) {
            int index = 0;
            int archerLine = archerSet[archer];
            //가장 가까운거리부터 시작합니다. 0~D-1까지 순회
            for(int distance=0; distance<D; distance++) {
                //좌측부터 순회합니다.
                int startLine = Math.max(0, archerLine-distance);
                int endLine = Math.min(M-1, archerLine+distance);
                for(int line=startLine; line<=endLine; line++) {
                    int lineIndex = distance - Math.abs(archerLine-line);
                    attackRange[archer][index++] = line*N + lineIndex;
                }
            }
        }
    }


    // 적이 한칸씩 이동하는 함수.
    // 동시에 적의 존재 여부틀 통해 게임의 끝 여부를 리턴합니다.
    static void enemyTurn() {
        remainder = 0;
        for(int line=0; line<M; line++) {
            bitmaskMap[line] >>= 1;
            remainder += Integer.bitCount(bitmaskMap[line]);
        }
    }


    //비트마스크 연산이 익숙치 않아서 line과 lineIndex값으로 bitmaskMap에 접근 및 조작할 수 있도록 만든 함수입니다.
    private static boolean isEnemyAt(int encodedRC) {
        int line = encodedRC/N;
        int index = encodedRC % N;
        return (bitmaskMap[line] & (1<<index)) != 0;
    }

    private static void setKillAt(int encodedRC) {
        int line = encodedRC/N;
        int index = encodedRC % N;
        bitmaskMap[line] &= ~(1<<index);
    }


    // nextCombination 이네용..
    // 매번 다음 컴비네이션을 업데이트 합니다.
    static boolean setNextArcherPosition() {
        if(archerSet[2] != M-1){
            archerSet[2]++;
            return true;
        } else if(archerSet[1] != M-2) {
            archerSet[1]++;
            archerSet[2] = archerSet[1] + 1;
            return true;
        } else if(archerSet[0] != M-3) {
            archerSet[0]++;
            archerSet[1] = archerSet[0]+1;
            archerSet[2] = archerSet[1]+1;
            return true;
        } else return false;
    }


    // 초기화 및 입력 반영 함수
    static void init() throws IOException {
        br = new BufferedReader(new InputStreamReader(System.in));
        st = new StreamTokenizer(br);

        N = nextInt();// 행의 갯수(맵의 길이)
        M = nextInt();// line의 갯수
        D = nextInt();// 최대공격거리

        receivedMap = new int[M];

        //비트마스크 맵에 입력을 반영합니다.
        //receivedMap[line] 은 열 1개를 표현합니다.
        for(int index=N; index>0; index--) {
            for(int line=0; line<M; line++) {
                receivedMap[line] <<= 1;
                receivedMap[line] |= nextInt();
            }
        }

        //전체 적의 수를 셉니다.
        enemyNumber =0;
        for(int line=0; line<M; line++) {
            enemyNumber += Integer.bitCount(receivedMap[line]);
        }

        estimatedPossibleMaxKillNum = Math.min(enemyNumber, 3*N);

        maxKill=0;

        int attackRangeNum = D*D;
        attackRange = new int[3][];
        attackRange[0] = new int[attackRangeNum];
        attackRange[1] = new int[attackRangeNum];
        attackRange[2] = new int[attackRangeNum];

        archerSet = new int[3];
        for(int i=0; i<3; i++) {
            archerSet[i] = i;
        }
    }


    //디버깅을 위한 맵 프린트 함수
    static void print() {
        StringBuilder sb = new StringBuilder();
        for(int l=0; l<M; l++) {
            String binary = String.format("%" + 15 + "s", Integer.toBinaryString(bitmaskMap[l])).replace(' ', '0');
            sb.append(l).append(": ").append(binary).append("\n");
        }
        System.out.println(sb);
    }

    static int nextInt() throws IOException {
        st.nextToken();
        return (int) st.nval;
    }
}
