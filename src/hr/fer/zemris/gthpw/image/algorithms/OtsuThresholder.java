package hr.fer.zemris.gthpw.image.algorithms;
public class OtsuThresholder {

	public static int getThreshold(int[] srcData, int max) {
		int threshold;
		int[] histData = new int[max];
		
		// Calculate histogram
		int ptr = 0;
		while (ptr < srcData.length) {
		   int h = srcData[ptr];
		   histData[h] ++;
		   ptr ++;
		}

		// Total number of pixels
		int total = srcData.length;

		float sum = 0;
		for (int t=0 ; t<max ; t++) sum += t * histData[t];

		float sumB = 0;
		int wB = 0;
		int wF = 0;

		float varMax = 0;
		threshold = 0;

		for (int t=0 ; t<max ; t++) {
		   wB += histData[t];               // Weight Background
		   if (wB == 0) continue;

		   wF = total - wB;                 // Weight Foreground
		   if (wF == 0) break;

		   sumB += (float) (t * histData[t]);

		   float mB = sumB / wB;            // Mean Background
		   float mF = (sum - sumB) / wF;    // Mean Foreground

		   // Calculate Between Class Variance
		   float varBetween = (float)wB * (float)wF * (mB - mF) * (mB - mF);

		   // Check if new maximum found
		   if (varBetween > varMax) {
		      varMax = varBetween;
		      threshold = t;
		   }
		}
		
		return threshold;
	}
	
}