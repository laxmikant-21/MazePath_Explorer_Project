import java.util.*;

public class MazeSolverLogic {
    public enum Algorithm { DFS, BFS }

    public static class Node {
        int x, y;
        Node parent;

        Node(int x, int y, Node parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }
    }

    public static List<int[]> solve(int[][] maze, int startX, int startY, int endX, int endY, Algorithm algo) {
        switch (algo) {
            case DFS: return dfs(maze, startX, startY, endX, endY);
            case BFS: return bfs(maze, startX, startY, endX, endY);
            default: return new ArrayList<>();
        }
    }

    private static List<int[]> dfs(int[][] maze, int x, int y, int endX, int endY) {
        Stack<Node> stack = new Stack<>();
        stack.push(new Node(x, y, null));
        boolean[][] visited = new boolean[maze.length][maze[0].length];

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            if (node.x == endX && node.y == endY)
                return constructPath(node);

            if (visited[node.y][node.x]) continue;
            visited[node.y][node.x] = true;

            for (int[] d : directions()) {
                int nx = node.x + d[0], ny = node.y + d[1];
                if (isValid(maze, visited, nx, ny))
                    stack.push(new Node(nx, ny, node));
            }
        }
        return new ArrayList<>();
    }

    private static List<int[]> bfs(int[][] maze, int x, int y, int endX, int endY) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(x, y, null));
        boolean[][] visited = new boolean[maze.length][maze[0].length];

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (node.x == endX && node.y == endY)
                return constructPath(node);

            if (visited[node.y][node.x]) continue;
            visited[node.y][node.x] = true;

            for (int[] d : directions()) {
                int nx = node.x + d[0], ny = node.y + d[1];
                if (isValid(maze, visited, nx, ny))
                    queue.add(new Node(nx, ny, node));
            }
        }
        return new ArrayList<>();
    }

    private static List<int[]> constructPath(Node node) {
        List<int[]> path = new ArrayList<>();
        while (node != null) {
            path.add(0, new int[]{node.x, node.y});
            node = node.parent;
        }
        return path;
    }

    private static boolean isValid(int[][] maze, boolean[][] visited, int x, int y) {
        return x >= 0 && y >= 0 && y < maze.length && x < maze[0].length && maze[y][x] != 1 && !visited[y][x];
    }

    private static int[][] directions() {
        return new int[][] { {0,1}, {1,0}, {0,-1}, {-1,0} };
    }
}
