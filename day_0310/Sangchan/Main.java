package offline.day_0310.Sangchan;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/*
 * N * M 맵
 * 
 * N+1 행은 성 (궁수 배치 공간)
 * 
 * 궁수 3명 배치
 * 
 * 0행부터 N행까지 적이 이동함
 * 
 * 궁수
 * - 공격 범위 : 거리가 D 이하 ( 가장 가까운 적, 가장 왼쪽의 적 )
 *  - 거리 : |x1 - x2| + |y1 - y2|
 * - 적 공격 시 : 해당 적 제거 + 처치 count++
 * - 주의 : 모든 궁수는 동시에 공격하므로, 궁수 한명이 공격했다고 적이 사라지는 게 아님!
 * 
 * 턴
 * - 공격
 * - 이동 (적이 성에 도달하면 게임 제외)
 * 
 * 문제) 배치한 이후, 궁수의 공격으로 제거할 수 있는 적의 최대 수 출력 (궁수 배치를 바꿔가며)
 * 
 * 입력)
 * row col range
 * 격자판상태 (0 = 빈칸, 1 = 적이 있음)
 * 
 * 문제 풀이)
 * 
 * 1. 테케 입력 받기
 * 
 * 2. 궁수 위치 바꾸는 함수
 * 
 * 3. 궁수가 공격하는 함수
 * 
 * 4. 적이 턴 지날 때마다 1씩 밑으로 내려가기
 * 
 * 5. 이를 적의 수가 0이 될 때까지 반복
 * 
 * ----------------------------------
 * 
 * 커스텀 클래스
 * --------
 * 1) 적 노드 - 현재 y위치와 다음 적을 가리킴
 * 2) 적 리스트 - head, tail로 적 노드를 가리킴
 * 위의 두 클래스를 통해 링크드리스트를 구현함
 * 적 리스트의 리스트로 적의 x좌표에 대한 각각의 y좌표를 가지는 자료구조 구현
 * 
 * 장점 -> 적 이동 시에 head tail값만 바꾸면 되서 오버헤드가 적음
 * 
 * 3) 궁수 - 현재 y위치와 공격 가능한 적 리스트를 가짐
 * 공격 가능한 적 리스트의 인덱스는 적과 궁수 사이의 거리를 의미함
 * 따라서 리스트를 탐색하면 처음 발견하는 적이 바로 최우선 공격 대상
 * 궁수 배열의 크기를 3으로 하고 조합으로 궁수 위치를 지정해서 관리함
 * 
 * - 공격 가능한 적 리스트
 *      - distance가 같을 경우 y좌표가 더 작은 적을 대상으로 해아함
 * 
 * --------
 * 
 * 1. 궁수 위치를 바꾸기
 *  1-1. 0 ~ N-1 까지 숫자에서 3개를 뽑는 조합 -> 궁수 colIndex 3개 뽑기
 *  1-2. 공격 가능한 대상을 추림 (궁수 위치, 공격 가능 범위 탐색 - 왼쪽부터)
 *       1-2-1. 각 궁수마다 적 리스트를 탐방하면서 적과의 거리를 비교함
 *       1-2-2. 사거리 안에 있으면 해당 적을 궁수의 공격 가능한 적 리스트에 등록
 *  1-3. 공격할 대상을 결정함
 *      1-3-1. 궁수를 탐방하면서 공격 가능한 적 리스트의 최우선 공격대상을 찾음
 *      1-3-2. 해당 적의 좌표를 Set에 추가
 *  1-4. 적을 공격함
 *      1-4-1. Set을 탐색하며 적을 공격
 *      1-4-2. 적 제거 카운트++
 *  1-5. 최대값 갱신
 * 
 */

public class Main {
    static BufferedReader br;
    static StringTokenizer st;

    static class EnemyNode {
        int currentY;
        EnemyNode next;

        public EnemyNode(int currentY) {
            this.currentY = currentY;
            next = null;
        }

        public EnemyNode(int currentY, EnemyNode next) {
            this.currentY = currentY;
            this.next = next;
        }
    }

    static class EnemyList {
        EnemyNode head;
        EnemyNode tail;

        public EnemyList() {
            this.head = null;
            this.tail = null;
        }

        public EnemyList(EnemyNode node) {
            this.head = node;
            this.tail = node;
        }

        public void reset(){
            this.head = null;
            this.tail = null;
        }

        // 뒤에 넣기
        public void addEnemy(EnemyNode newEnemy){
            if(this.head == null){
                this.head = newEnemy;
                this.tail = newEnemy;
                return;
            }

            EnemyNode beforeLastEnemy = this.tail;
            this.tail = newEnemy;
            beforeLastEnemy.next = newEnemy;
        }

        public boolean deleteEnemy(int index) {
            EnemyNode beforeEnemy = null;
            EnemyNode currentEnemy = this.head;
        
            while (currentEnemy != null) {
                if (currentEnemy.currentY == index) {
                    if (beforeEnemy == null) {
                        this.head = currentEnemy.next;
                        
                        if (this.head == null) {
                            this.tail = null;
                        }
                    } else {
                        beforeEnemy.next = currentEnemy.next;
                        if (currentEnemy == this.tail) {
                            this.tail = beforeEnemy;
                        }
                    }
                    return true;
                }
                beforeEnemy = currentEnemy;
                currentEnemy = currentEnemy.next;
            }

            return false;
        }
        
    }

    static class Archer extends EnemyNode {
        //첫번쨰 리스트의 index는 distance를 의미한다.
        ArrayList<int[]> attackEnemyList;

        public Archer(int currentY) {
            super(currentY);
            attackEnemyList = new ArrayList<>(Collections.nCopies(COL_COUNT + ROW_COUNT, null));
        }

        public void addAttackEnemy(int distance, int[] enemyPosition){
            if(attackEnemyList.get(distance) == null){
                attackEnemyList.set(distance, enemyPosition);
            } else {
                int[] aleadyAttackEnemy = attackEnemyList.get(distance);

                if(aleadyAttackEnemy[1] > enemyPosition[1]){
                    attackEnemyList.set(distance, enemyPosition);
                }
            }
        }

        public void reset(){
            attackEnemyList = new ArrayList<>(Collections.nCopies(COL_COUNT + ROW_COUNT, null));
        }
    }

    static int ROW_COUNT;
    static int COL_COUNT;
    static int attackRange;
    static int[][] ORIGIN_MAP;
    static Archer[] archers;
    static int[] archerPlaceColIndexs;
    static int MAX_KILL_COUNT;
    static int currentKillCount;
    static List<EnemyList> enemyInfoList;
    static Set<String> attackEnemy;
    static void init() throws IOException {
        st = new StringTokenizer(br.readLine().trim());

        ROW_COUNT = Integer.parseInt(st.nextToken());
        COL_COUNT = Integer.parseInt(st.nextToken());
        attackRange = Integer.parseInt(st.nextToken());
        MAX_KILL_COUNT = Integer.MIN_VALUE;
        currentKillCount = 0;
        
        ORIGIN_MAP = new int[ROW_COUNT][COL_COUNT];

        archerPlaceColIndexs = new int[COL_COUNT];

        for(int index=0; index<COL_COUNT;index++){
            archerPlaceColIndexs[index] = index;
        }

        enemyInfoList = new LinkedList<>();
        archers = new Archer[3];

        for(int rowIndex = 0; rowIndex < ROW_COUNT; rowIndex++){
            st = new StringTokenizer(br.readLine().trim());
            enemyInfoList.add(new EnemyList());
            for(int colIndex = 0; colIndex < COL_COUNT; colIndex++){
                int placeInfo = Integer.parseInt(st.nextToken());
                ORIGIN_MAP[rowIndex][colIndex] = placeInfo;

                if(placeInfo != 0){
                    enemyInfoList.get(rowIndex).addEnemy(new EnemyNode(colIndex));
                }
            }
        }

        attackEnemy = new HashSet<>();
    }

    public static void main(String[] args) throws IOException {
        br = new BufferedReader(new InputStreamReader(System.in));

        init();
        selectArcherPosition(0,0);

        System.out.print(MAX_KILL_COUNT);
        br.close();
    }

    static void initSenario(){
        currentKillCount = 0;
        // 적 위치 초기화
        for(int rowIndex = 0; rowIndex < ROW_COUNT; rowIndex++){
            enemyInfoList.get(rowIndex).reset();
            for(int colIndex = 0; colIndex < COL_COUNT; colIndex++){
                int placeInfo = ORIGIN_MAP[rowIndex][colIndex];

                if(placeInfo != 0){
                    enemyInfoList.get(rowIndex).addEnemy(new EnemyNode(colIndex));
                }
            }
        }
    }

    static void selectArcherPosition(int selectIndex, int elementIndex){
        if(selectIndex == 3){
            // 공격 시물레이션 시작
            attackSenario();
            return;
        }

        if(elementIndex == COL_COUNT){
            return;
        }

        archers[selectIndex] = new Archer(archerPlaceColIndexs[elementIndex]);
        selectArcherPosition(selectIndex + 1, elementIndex + 1);
        selectArcherPosition(selectIndex, elementIndex + 1);
    }

    static void attackSenario(){
        initSenario();

        int turn = 0;
        
        while(turn++ < ROW_COUNT){
            shootEnemy();
            moveEnemy();
        }

        MAX_KILL_COUNT = Math.max(MAX_KILL_COUNT, currentKillCount);
    }

    static void shootEnemy(){
        // 1. 각 궁수마다 공격 가능한 적들 리스트를 업데이트 한다.
        for(int archerIndex = 0; archerIndex < 3; archerIndex++) {
            Archer archer = archers[archerIndex]; // 현재 궁수 위치는 N, archer,currentY

            // 적의 맨 밑에서 부터 공격 가능한지 확인
            for(int rowIndex = ROW_COUNT-1; rowIndex >= 0; rowIndex--){
                if(enemyInfoList.get(rowIndex).head == null){
                    continue;
                }
                
                for(EnemyNode enemy = enemyInfoList.get(rowIndex).head ; enemy != null ; enemy = enemy.next){
                    int[] enemyPosition = new int[]{rowIndex, enemy.currentY};
                    int distance = getDistance(enemyPosition, new int[]{ROW_COUNT, archer.currentY});

                    if(!isInAttackRange(distance)){
                        continue;
                    }

                    // 공격 가능 범위라면 -> List에 넣기 -> 해당 인덱스(거리)의 맨 앞에 있을 수록 우선순위 높음
                    archer.addAttackEnemy(distance, enemyPosition);
                }
            }
        }

        // 2. 각 궁수들의 공격 가능한 적 리스트에서 가장 우선순위가 높은 적을 Set에 담는다 ( 중복 안되게 )
        for(int archerIndex = 0; archerIndex < 3; archerIndex++) {
            Archer archer = archers[archerIndex]; // 현재 궁수 위치는 N, archer

            for(int range = 0; range < COL_COUNT + ROW_COUNT; range++) {
                if(archer.attackEnemyList.get(range) != null){
                    int[] enemyPosition = archer.attackEnemyList.get(range);
                    attackEnemy.add(getEnemyInfo(enemyPosition));
                    archer.reset();
                    break;
                }
            }
        }
        // 3. 해당 위치의 적을 제거한다.
        for(String enemyInfo : attackEnemy){
            int[] enemyPosition = Arrays.stream(enemyInfo.split(" ")).mapToInt(Integer::parseInt).toArray();
            if(enemyInfoList.get(enemyPosition[0]).deleteEnemy(enemyPosition[1])){
                currentKillCount++;
            }
        }

        // 4. Set을 초기화
        attackEnemy.clear();
    }

    static void moveEnemy(){
        // 맨 밑의 줄 비우기
        enemyInfoList.get(ROW_COUNT - 1).reset();

        // 차례대로 밑으로 이동
        for(int rowIndex = ROW_COUNT-2; rowIndex >= 0; rowIndex--){
            if(enemyInfoList.get(rowIndex).head != null){
                enemyInfoList.get(rowIndex + 1).head = enemyInfoList.get(rowIndex).head;
                enemyInfoList.get(rowIndex + 1).tail = enemyInfoList.get(rowIndex).tail;
                enemyInfoList.get(rowIndex).head = null;
                enemyInfoList.get(rowIndex).tail = null;
            }
        }
    }

    static String getEnemyInfo(int[] enemyPosition){
        return enemyPosition[0] + " " + enemyPosition[1];
    }
    static boolean isInAttackRange(int distance){
        return distance <= attackRange;
    }
    static int getDistance(int[] archer, int[] enemy){
        return Math.abs(archer[0] - enemy[0]) + Math.abs(archer[1] - enemy[1]);
    }
}
