package app;

public class Move {

    public String move_name;
    public boolean implemented;
    public boolean status;
    public Type type;

    @Override public String toString() {
        return move_name + "= type:" + type + ", implemented:" + implemented + ", status:" + status;
    }

}
