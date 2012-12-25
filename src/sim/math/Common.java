package sim.math;

public class Common {

    //  Lanczos Gamma Function approximation - small gamma
    private static double lgfGamma = 5.0;

    // 	Lanczos Gamma Function approximation - Coefficients
    private static double[] lgfCoeff = {1.000000000190015, 76.18009172947146, -86.50532032941677, 24.01409824083091, -1.231739572450155, 0.1208650973866179E-2, -0.5395239384953E-5};

    //  Lanczos Gamma Function approximation - N (number of coefficients -1)
    private static int lgfN = 6;

    // factorial of n
    // Argument is of type double but must be, numerically, an integer
    // factorial returned as double but is, numerically, should be an integer
    // numerical rounding may makes this an approximation after n = 21
	// stolen from: Dr Michael Thomas Flanagan
	// http://www.ee.ucl.ac.uk/~mflanaga/java/Stat.java
    public static double factorial(int n){
            if(n<0 || (n-(int)n)!=0)
            	throw new IllegalArgumentException("\nn must be a positive integer\nIs a Gamma funtion [gamma(x)] more appropriate?");
            double f = 1.0D;
            for(int i=1; i<=n; i++)f*=i;
            return f;
    }

    // Gamma function
    // Lanczos approximation (6 terms)
	// stolen from: Dr Michael Thomas Flanagan
	// http://www.ee.ucl.ac.uk/~mflanaga/java/Stat.java
    public static double gamma(double x){

            double xcopy = x;
            double first = x + lgfGamma + 0.5;
            double second = lgfCoeff[0];
            double fg = 0.0D;

            if(x>=0.0){
                    if(x>=1.0D && x-(int)x==0.0D){
                            fg = factorial((int)x)/x;
                    }
                    else{
                            first = Math.pow(first, x + 0.5)*Math.exp(-first);
                            for(int i=1; i<=lgfN; i++)second += lgfCoeff[i]/++xcopy;
                            fg = first*Math.sqrt(2.0*Math.PI)*second/x;
                    }
            }
            else{
                     fg = -Math.PI/(x*gamma(-x)*Math.sin(Math.PI*x));
            }

            return fg;
    }

}
