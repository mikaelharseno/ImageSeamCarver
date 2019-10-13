
import java.awt.Color;
import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private Picture picture;
    private int width, height;
    private Pixel[][] data;

    private class Pixel {
        double energy, minPathEnergyH, minPathEnergyV;
        int r, g, b;
        int horizontal, vertical;

        Pixel(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
            horizontal = 100;
            vertical = 100;
        }

        public double energy() {
            return energy;
        }

    }

    public SeamCarver(Picture picture) {
        width = picture.width();
        height = picture.height();
        this.picture = new Picture(picture);
        data = new Pixel[height][width];
        Color cur;

        for (int i = 0; i < height; i = i + 1) {
            for (int j = 0; j < width; j = j + 1) {
                cur = picture.get(j, i);
                data[i][j] = new Pixel(cur.getRed(), cur.getGreen(), cur.getBlue());
            }
        }

        for (int i = height - 1; i >= 0; i = i - 1) {
            for (int j = 0; j < width; j = j + 1) {
                data[i][j].energy = calculateEnergy(i, j);
                findLowestEnergyPath(i, j);
            }
        }

        transpose();

        for (int i = height - 1; i >= 0; i = i - 1) {
            for (int j = 0; j < width; j = j + 1) {
                data[i][j].energy = calculateEnergy(i, j);
                findLowestEnergyPath(i, j);
            }
        }

        transpose();
    }

    private void transpose() {
        Pixel[][] newdata = new Pixel[width][height];
        int temp = width;
        width = height;
        height = temp;
        double temp2;
        for (int i = 0; i < height; i = i + 1) {
            for (int j = 0; j < width; j = j + 1) {
                newdata[i][j] = data[j][i];
                temp2 = data[j][i].minPathEnergyV;
                data[j][i].minPathEnergyV = data[j][i].minPathEnergyH;
                data[j][i].minPathEnergyH = temp2;
                temp = data[j][i].horizontal;
                data[j][i].horizontal = data[j][i].vertical;
                data[j][i].vertical = temp;
            }
        }
        this.data = newdata;
    }


    private void findLowestEnergyPath(int row, int column) {
        Pixel cur = data[row][column], botLeft, botMid, botRight;
        if (row == height - 1) {
            cur.minPathEnergyV = data[row][column].energy();
        } else {
            if (width == 1) {
                botMid = data[row + 1][column];
                botMid.minPathEnergyV = cur.energy + botMid.minPathEnergyV;
                data[row][column].vertical = 0;
            } else if (column == 0) {
                botMid = data[row + 1][column];
                botRight = data[row + 1][column + 1];
                if (botMid.minPathEnergyV >= botRight.minPathEnergyV) {
                    cur.minPathEnergyV = cur.energy + botRight.minPathEnergyV;
                    cur.vertical = 1;
                } else {
                    cur.minPathEnergyV = cur.energy + botMid.minPathEnergyV;
                    cur.vertical = 0;
                }
            } else if (column == width - 1) {
                botLeft = data[row + 1][column - 1];
                botMid = data[row + 1][column];
                if (botMid.minPathEnergyV >= botLeft.minPathEnergyV) {
                    cur.minPathEnergyV = cur.energy + botLeft.minPathEnergyV;
                    cur.vertical = -1;
                } else {
                    cur.minPathEnergyV = cur.energy + botMid.minPathEnergyV;
                    cur.vertical = 0;
                }
            } else {
                botLeft = data[row + 1][column - 1];
                botMid = data[row + 1][column];
                botRight = data[row + 1][column + 1];

                if ((botLeft.minPathEnergyV <= botMid.minPathEnergyV)
                        && (botLeft.minPathEnergyV <= botRight.minPathEnergyV)) {
                    cur.minPathEnergyV = cur.energy + botLeft.minPathEnergyV;
                    cur.vertical = -1;
                } else if ((botMid.minPathEnergyV <= botLeft.minPathEnergyV)
                        && (botMid.minPathEnergyV <= botRight.minPathEnergyV)) {
                    cur.minPathEnergyV = cur.energy + botMid.minPathEnergyV;
                    cur.vertical = 0;
                } else {
                    cur.minPathEnergyV = cur.energy + botRight.minPathEnergyV;
                    cur.vertical = 1;
                }
            }
        }
    }

    private double calculateEnergy(int row, int column) {
        Pixel left, right, top, bottom;
        if (column > 0) {
            left = data[row][column - 1];
        } else {
            left = data[row][width - 1];
        }
        if (column < width - 1) {
            right = data[row][column + 1];
        } else {
            right = data[row][0];
        }
        if (row > 0) {
            top = data[row - 1][column];
        } else {
            top = data[height - 1][column];
        }
        if (row < height - 1) {
            bottom = data[row + 1][column];
        } else {
            bottom = data[0][column];
        }

        return Math.pow(left.r - right.r, 2) + Math.pow(left.g - right.g, 2)
                + Math.pow(left.b - right.b, 2) + Math.pow(top.r - bottom.r, 2)
                + Math.pow(top.g - bottom.g, 2) + Math.pow(top.b - bottom.b, 2);
    }


    public Picture picture() {
        return new Picture(picture);
    }

    public     int width() {
        return width;
    }

    public     int height()       {
        return height;
    }

    public  double energy(int x, int y)    {
        return data[y][x].energy();
    }

    public   int[] findHorizontalSeam() {
        int rowSmallest = 0;
        double smallestValue = data[0][0].minPathEnergyH;
        if (height > 1) {
            for (int i = 1; i < height; i = i + 1) {
                if (data[i][0].minPathEnergyH < smallestValue) {
                    rowSmallest = i;
                    smallestValue = data[i][0].minPathEnergyH;
                }
            }
        }
        int[] result = new int[width];
        result[0] = rowSmallest;
        rowSmallest = rowSmallest + data[rowSmallest][0].horizontal;
        if (width > 1) {
            for (int i = 1; i < width; i += 1) {
                result[i] = rowSmallest;
                rowSmallest = rowSmallest + data[rowSmallest][i].horizontal;
            }
        }
        return result;
    }

    public   int[] findVerticalSeam() {
        int colSmallest = 0;
        double smallestValue = data[0][0].minPathEnergyV;
        if (width > 1) {
            for (int i = 1; i < width; i = i + 1) {
                if (data[0][i].minPathEnergyV < smallestValue) {
                    colSmallest = i;
                    smallestValue = data[0][i].minPathEnergyV;
                }
            }
        }
        int[] result = new int[height];
        result[0] = colSmallest;
        colSmallest = colSmallest + data[0][colSmallest].vertical;
        if (height > 1) {
            for (int i = 1; i < height; i += 1) {
                result[i] = colSmallest;
                colSmallest = colSmallest + data[i][colSmallest].vertical;
            }
        }
        return result;
    }

    public    void removeHorizontalSeam(int[] seam) {
        Picture newpicture = SeamRemover.removeHorizontalSeam(picture, seam);
        this.picture = newpicture;
        width = picture.width();
        height = picture.height();
        data = new Pixel[height][width];
        Color cur;

        for (int i = 0; i < height; i = i + 1) {
            for (int j = 0; j < width; j = j + 1) {
                cur = picture.get(j, i);
                data[i][j] = new Pixel(cur.getRed(), cur.getGreen(), cur.getBlue());
            }
        }

        for (int i = height - 1; i >= 0; i = i - 1) {
            for (int j = 0; j < width; j = j + 1) {
                data[i][j].energy = calculateEnergy(i, j);
                findLowestEnergyPath(i, j);
            }
        }

        transpose();

        for (int i = height - 1; i >= 0; i = i - 1) {
            for (int j = 0; j < width; j = j + 1) {
                data[i][j].energy = calculateEnergy(i, j);
                findLowestEnergyPath(i, j);
            }
        }

        transpose();
    }

    public    void removeVerticalSeam(int[] seam) {
        Picture newpicture = SeamRemover.removeVerticalSeam(picture, seam);
        this.picture = newpicture;
        width = picture.width();
        height = picture.height();
        data = new Pixel[height][width];
        Color cur;

        for (int i = 0; i < height; i = i + 1) {
            for (int j = 0; j < width; j = j + 1) {
                cur = picture.get(j, i);
                data[i][j] = new Pixel(cur.getRed(), cur.getGreen(), cur.getBlue());
            }
        }

        for (int i = height - 1; i >= 0; i = i - 1) {
            for (int j = 0; j < width; j = j + 1) {
                data[i][j].energy = calculateEnergy(i, j);
                findLowestEnergyPath(i, j);
            }
        }

        transpose();

        for (int i = height - 1; i >= 0; i = i - 1) {
            for (int j = 0; j < width; j = j + 1) {
                data[i][j].energy = calculateEnergy(i, j);
                findLowestEnergyPath(i, j);
            }
        }

        transpose();
    }
}
