class SortUtils{
	/**  
 	* 冒泡法排序  

 	* 比较相邻的元素。如果第一个比第二个大，就交换他们两个。  
 	* 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。在这一点，最后的元素应该会是最大的数。  
 	* 针对所有的元素重复以上的步骤，除了最后一个。
 	* 持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。  

 	*   
 	* @param numbers  
 	*            需要排序的整型数组  
 	*/  
 	public static void bubbleSort (int[] numbers){
		int size = numbers.length; //数组大小
		for(int i = 0; i < size - 1; i++)
			for(int j = i; j < size; j++){
				if(numbers[i] > numbers[j]){
					numbers[i] = numbers[i] ^ numbers[j];
					numbers[j] = numbers[i] ^ numbers[j];
					numbers[i] = numbers[i] ^ numbers[j];
				}
			}
 	}
 
 	/**  
 	* 选择排序
 	* 在未排序序列中找到最大元素，存放到排序序列的末尾位置。
 	* 再从剩余未排序元素中继续寻找最大元素，然后放到排序序列末尾。 
 	* 以此类推，直到所有元素均排序完毕。  

 	*   
 	* @param numbers
 	*			  需要排序的整型数组
 	*/  
 	public static void selectSort(int[] numbers){
		int size = numbers.length;
		for(int i = 0; i < size; i++){
			int position = i;
			int temp = 0;
			for(int j = i+1; j < size; j++){
				if(numbers[position] > numbers[j])
					position = j;
			}
			//异或交换测试失败
			//numbers[position] = numbers[position] ^ numbers[i];
			//numbers[i] = numbers[position] ^ numbers[i];
			//numbers[position] = numbers[position] ^ numbers[i];
			//算数交换测试失败
			//numbers[position] = numbers[position] + numbers[i];
			//numbers[i] = numbers[position] - numbers[i];
			//numbers[position] = numbers[position] - numbers[i];
			temp = numbers[i];   
			numbers[i] = numbers[position];   
			numbers[position] = temp;
		}
 	}
	/**  
 	* 快速排序
 	* 
 	* 从数列中挑出一个元素，称为“基准”  
 	* 重新排序数列，所有元素比基准值小的摆放在基准前面，所有元素比基准值大的摆在基准的后面（相同的数可以到任一边）。在这个分割之后，
   	  该基准是它的最后位置。这个称为分割（partition）操作。
 	* 递归地把小于基准值元素的子数列和大于基准值元素的子数列排序。  
 	*   
 	* @param numbers  待排序数组
 	* @param start  数组起始位置
 	* @param end  数组结束位置
 	*/  
	public static void quickSort(int[] numbers, int start, int end) {   
    	if (start < end) {   
        	int base = numbers[start]; // 选定的基准值（第一个数值作为基准值）   
        	int temp; // 记录临时中间值   
        	int startIndex = start //定义起始索引
        	int endIndex = end;   //定义结尾索引
        	do { 
        		/*如果起始索引值小于基准值，并且起始索引小于结束位置，起始索引循环后移*/
         		while ((numbers[startIndex] < base) && (startIndex < end))   
            	    startIndex++;
            	/*如果结尾索引值大于基准值，并且结尾索引大于起始位置，结尾索引循环前移*/
            	while ((numbers[endIndex] > base) && (endIndex > start))   
                	endIndex--;
                /*如果起始索引小于等于结尾索引，交换索引值，并且起始索引后移，结束索引前移*/
            	if (startIndex <= endIndex) {   
                	temp = numbers[startIndex];   
             		numbers[startIndex] = numbers[endIndex];   
                	numbers[endIndex] = temp;   
                	startIndex++;   
                	endIndex--;   
            	}   
        	} while (startIndex <= endIndex);   
        	if (start < endIndex)   
            	quickSort(numbers, start, endIndex); //递归排序前半段  
        	if (end > startIndex)   
            	quickSort(numbers, startIndex, end);   //递归排序后半段
    	}   
	}  
}
