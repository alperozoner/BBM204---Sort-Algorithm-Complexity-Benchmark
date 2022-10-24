import java.util.Arrays;

public class PigeonHole {
    public static void sort(Integer[] a, int n, int min, int max)
    {
        int range, i, j, index;
        range = max - min + 1;
        int[] phole = new int[range];
        Arrays.fill(phole, 0);

        for (i = 0; i < n; i++) phole[a[i] - min]++;

        index = 0;

        for (j = 0; j < range; j++)
            while (phole[j]-- > 0)
                a[index++] = j + min;
    }
}
