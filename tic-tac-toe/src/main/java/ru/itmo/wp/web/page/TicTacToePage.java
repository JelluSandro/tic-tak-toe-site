package ru.itmo.wp.web.page;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class TicTacToePage {

    public static void action(HttpServletRequest request, Map<String, Object> data) {
        State state = (State) request.getSession().getAttribute("state");
        if (state == null) {
            state = new State(3);
        }
        data.put("state", state);
        request.getSession().setAttribute("state", state);
    }

    public static void onMove(HttpServletRequest request, Map<String, Object> data) {
        State state = (State) request.getSession().getAttribute("state");
        if (Objects.equals(state.phase, "RUNNING")) {
            int size = state.size;
            Character[][] cells = state.cells;
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    String name = "cell_" + i + j;
                    String where = request.getParameter(name);
                    if (where != null && cells[i][j] == null) {
                        state.cnt++;
                        cells[i][j] = state.crossesMove ? 'X' : 'O';
                        state.crossesMove ^= true;
                        break;
                    }
                }
            }
            for (int i = 0; i < size; ++i) {
                boolean win = true;
                for (int j = 1; j < size; ++j) {
                    if (cells[i][j] != cells[i][j - 1]) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    state.setPhase(cells[i][0]);
                }
            }
            for (int j = 0; j < size; ++j) {
                boolean win = true;
                for (int i = 1; i < size; ++i) {
                    if (cells[i][j] != cells[i - 1][j]) {
                        win = false;
                        break;
                    }
                }
                if (win) {
                    state.setPhase(cells[0][j]);
                }
            }
            boolean win = true;
            for (int i = 1; i < size; ++i) {
                if (cells[i][i] != cells[i - 1][i - 1]) {
                    win = false;
                    break;
                }
            }
            if (win) {
                state.setPhase(cells[0][0]);
            }
            win = true;
            for (int i = 1; i < size; ++i) {
                if (cells[i][size - i - 1] != cells[i - 1][size - i]) {
                    win = false;
                    break;
                }
            }
            if (win) {
                state.setPhase(cells[0][size - 1]);
            }

            if (state.cnt == size * size && Objects.equals(state.phase, "RUNNING")) {
                state.phase = "DRAW";
            }
        }
        data.put("state", state);
        request.getSession().setAttribute("state", state);
    }

    public static void newGame(HttpServletRequest request, Map<String, Object> data) {
        State state = new State(3);

        data.put("state", state);
        request.getSession().setAttribute("state", state);
    }

    public static class State {
        int size;
        Character[][] cells;
        String phase;
        boolean crossesMove;
        int cnt;

        public State(int size) {
            this.size = size;
            cnt = 0;
            cells = new Character[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    cells[i][j] = null;
                }
            }
            phase = "RUNNING";
            crossesMove = true;
        }

        public int getSize() {
            return size;
        }

        public Character[][] getCells() {
            return cells;
        }

        public String getPhase() {
            return phase;
        }

        public boolean getCrossesMove() {
            return crossesMove;
        }

        public void setPhase(Character sym) {
            if (sym != null) {
                phase = "WON_" + sym;
            }
        }
    }
}
