#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <omp.h>
#define SIZE 1000
void setUp(int a[], int size);
void tearDown(double start, double end, int a[], int size);

void setUp(int a[], int size){
	int i;
	srand(time(NULL));
	for (i = 0; i<size; ++i) {
		a[i] = i*2;
		printf("%d\n",a[i]);    
	}
	//printf("Complete\n");
	return;
}

void tearDown(double start, double end, int a[], int size) {
	int sorted = 1;
	int i;

	printf("Time to execute: %f\n", end-start);
	
	for (i = 0; i < size-1; ++i) {
		sorted &= (a[i] <= a[i+1]);
	}
	
	printf("Array sorted: %d\n", sorted);
	#pragma omp parallel
	{
		#pragma omp master
		{
			printf("Num threads: %d\n", omp_get_num_threads());
		}
	}
}

/*
int parallelSearch(int arr[], int l, int r, int x,int num_process) 
{ 
   int step=(r-l)/num_process;
   int flag[num_process];
   if(l<r) return -1;
   
   #pragma omp parallel for
   for(int i=0;i<num_process;i++){
        flag[i]=0;
        if(x<arr[l+(step*i)]){
            flag[i]=1;
        }
        else if(x==arr[l+(step*i)]){
            return l+(step*i);
        }
   }
   
    //#pragma omp parallel for
   for(int i=0;i<num_process-1;i++){
   {
        if(flag[i]!=flag[i+1]){
            return parallelSearch(arr, l, l+(step*i), x, num_process); 
        }
   
   }
  
   return -1; 
} 
*/

int parallelSearch(int arr[], int l, int r, int x,int num_process)
{

  while (l <= r)
  {
    if((r-l-4)<num_process){
        binarySearch(arr,l,r,x);
    }
   int step=(r-l)/num_process;
   int flag[num_process];
   int f=-1;
   printf("L:%d R:%d \n",l,r);
   #pragma omp parallel for
   for(int i=0;i<num_process;i++){
        flag[i]=0;
        f=-1;
        if(x>arr[l+(step*i)]){
            flag[i]=1;
        }
        else if(x==arr[l+(step*i)]){
            f=l+(step*i);
        }
   }
   if(f!=-1)return f;
   
    #pragma omp parallel for
   for(int i=0;i<num_process-1;i++)
   {
        if(flag[i]!=flag[i+1]){
            l=l+(step*i);                                                                                    
            r=l+(step*(i+1));
        }
   
   }
   
  }
  return -1; 
}


int binarySearch(int arr[], int l, int r, int x) 
{ 
   if (r >= l) 
   { 
        int mid = l + (r - l)/2; 
  
        // If the element is present at the middle  
        // itself 
        if (arr[mid] == x)   
            return mid; 
  
        // If element is smaller than mid, then  
        // it can only be present in left subarray 
        if (arr[mid] > x)  
            return binarySearch(arr, l, mid-1, x); 
  
        // Else the element can only be present 
        // in right subarray 
        return binarySearch(arr, mid+1, r, x); 
   } 
  
   return -1; 
} 

int main() {

    //omp_set_nested(1);
    //omp_set_dynamic(0);     // Explicitly disable dynamic teams
    //omp_set_num_threads(1); // Use 4 threads for all consecutive parallel regions
    int x=2;
	int a[SIZE];
	//int temp[SIZE];
	double startTime, endTime;
	int num_threads;

	#pragma omp parallel
	{
		#pragma omp master
		{
			num_threads = omp_get_num_threads();
		}
	}
    printf("Parallel Thread Count:%d \n",num_threads);
	setUp(a, SIZE);
	startTime = omp_get_wtime();
	printf("Found at %d",parallelSearch(a,0,SIZE-1,x,num_threads));
	endTime = omp_get_wtime();
	tearDown(startTime, endTime, a, SIZE);
	
	
    printf("Serial :%d \n",num_threads);
	setUp(a, SIZE);
	startTime = omp_get_wtime();
	//binarySearch(a,0,SIZE-1,x);
	endTime = omp_get_wtime();
	tearDown(startTime, endTime, a, SIZE);
}
