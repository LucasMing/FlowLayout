# FlowLayout
# 自定义流式布局 #
自定义view主要实现onMeasure+onDraw,自定义ViewGroup主要实现onMeasure+onLayout
## onMeasure ##
1. 确定子view大小，确定子view坐标
	
	MeasureSpec是view的内部类，基本都是二进制运算，int类型所以有32位，封装了父布局传递给子布局的布局要求，每个MeasureSpec代表了一组宽度和高度的要求。用高两位表示mode,低30位表示size。在onMeasure方法中递归调用度量子view。
2. 确定自己的大小



