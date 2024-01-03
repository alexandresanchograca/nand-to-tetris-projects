//function Sys.init 0
@256
D=A
@SP
M=D
//push constant 4
@4
D=A
@SP
A=M
M=D
@SP
M=M+1
//call Main.fibonacci 1
//label WHILE
(WHILE)
//goto WHILE 
@WHILE
0;JMP
