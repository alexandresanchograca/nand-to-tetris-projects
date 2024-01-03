// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/08/FunctionCalls/SimpleFunction/SimpleFunction.vm

// Performs a simple calculation and returns the result.
// function SimpleFunction.test 2
//push local 0
// push local 1
// add
// not
// push argument 0
// add
// push argument 1
// sub
// return



//Setting our local segment (nArgs of function initialized)
//@SP
//D=M
//@LCL //Defenir o ponteiro de Local, não sei se é preciso...
//M=D

//push constant 0
@0
D=A
@SP
A=M
M=D
@SP
M=M+1
//push constant 0
@0
D=A
@SP
A=M
M=D
@SP
M=M+1

//push local 0
@LCL
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//push local 1
@LCL
D=M
@1
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//add
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=M+D
@SP
M=M+1
//not
@SP
M=M-1
A=M
M=!M
@SP
M=M+1
//push argument 0
@ARG
D=M
@0
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//add
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=M+D
@SP
M=M+1
//push argument 1
@ARG
D=M
@1
A=D+A
D=M
@SP
A=M
M=D
@SP
M=M+1
//sub
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
M=M-D
@SP
M=M+1

//Restauramos os ponteiros de segmentos de memoria
//Movemos LCL para R13
@LCL
D=M
@R13
M=D

//Copiamos o valor de retorno para o valor do ARG[0]
// ARG[0] = *SP
//OU pop argument 0
@SP
A=M-1
D=M 
@ARG
A=M 
M=D

//Restauramos o StackPointer para a posição antes de chamada
//SP = ARG + 1
@ARG
D=M+1
@SP 
M=D

//Restauramos os restantes segmentos de memória
//Sabemos que segundo o standar acime do LCL[0] ta o THAT
@LCL
A=M-1 
D=M
@THAT
M=D

@2
D=A
@LCL
A=M-D
D=M
@THIS
M=D

@3
D=A
@LCL
A=M-D
D=M
@ARG
M=D

@4
D=A
@LCL
A=M-D
D=M
@LCL
M=D