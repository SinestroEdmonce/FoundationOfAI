package Assignment2.EfficientPruningButMessy;

/**
 * @projectName FoAI-Assignments
 * @fileName Camp
 * @auther Qiaoyi Yin
 * @time 2019-10-18 15:25
 * @function Camp to store some constraints
 */

public class Camp {
    // Attributes
    int sideLengthOfCamp;

    // Camp center
    int []center;

    // Camp sides
    int []campLeft;
    int []campRight;
    int []campTopDown;

    // Type of camp
    PlayerType typeOfCamp;

    // Default construct
    Camp() {
        this.sideLengthOfCamp = 0;

        this.center = null;
        this.campLeft = null;
        this.campRight = null;
        this.campTopDown = null;

        this.typeOfCamp = PlayerType.UNKNOWN;
    }

    Camp(int sideLength) {
        this.sideLengthOfCamp = sideLength;

        this.center = null;
        this.campLeft = new int [sideLength];
        this.campRight = new int [sideLength];
        this.campTopDown = new int [2];

        this.typeOfCamp = PlayerType.UNKNOWN;
    }

    public void setCamp(PlayerType playerType, int height, int width)  {
        this.typeOfCamp = playerType;

        try {
            if (playerType == PlayerType.BLACK) {
                for (int itr=0; itr<this.sideLengthOfCamp; ++itr) {
                    this.campLeft[itr] = 0;
                    this.campRight[itr] = this.sideLengthOfCamp-itr;
                }
                // Deal with boundary
                this.campRight[0] -= 1;

                this.campTopDown[0] = 0;
                this.campTopDown[1] = this.sideLengthOfCamp-1;
                this.center = new int [] {0, 0};
            }
            else if (playerType == PlayerType.WHITE) {
                for (int itr=0; itr<this.sideLengthOfCamp; ++itr) {
                    this.campRight[itr] = width-1;
                    this.campLeft[itr] = width-1-(itr+1);
                }
                // Deal with boundary
                this.campLeft[this.sideLengthOfCamp-1] += 1;

                this.campTopDown[0] = height-this.sideLengthOfCamp;
                this.campTopDown[1] = height-1;
                this.center = new int [] {15, 15};
            }
            else {
                throw new Exception("Invalid type of camp.");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[] getCampLeft() {
        return this.campLeft;
    }

    public int[] getCampRight() {
        return this.campRight;
    }

    public int[] getCampTopDown() {
        return this.campTopDown;
    }

    public int getSideLengthOfCamp() {
        return this.sideLengthOfCamp;
    }

    public int getCampLeft(int index) {
        assert (index >= 0 && index < this.sideLengthOfCamp);

        return this.campLeft[index];
    }

    public int getCampRight(int index) {
        assert (index >= 0 && index < this.sideLengthOfCamp);

        return this.campRight[index];
    }

    public int getCampTop() {
        return this.campTopDown[0];
    }

    public int getCampDown() {
        return this.campTopDown[1];
    }

    public int[] getCenter() {
        return this.center;
    }
}
