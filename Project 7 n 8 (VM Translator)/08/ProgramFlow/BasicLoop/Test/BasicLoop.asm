//push constant 0 
@0
D=A
@SP
A=M
M=D
@SP
M=M+1


//pop local 0 storing the sum value in the local memory space sum = 0
@LCL
D=M
@0
D=D+A
@R13
M=D
@SP
M=M-1
A=M
D=M
@R13
A=M
M=D

//label LOOP_START sum+= counter e counter--
(LOOP_START.1)




//push argument 0 pushing the counter to the stack
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


//push local 0 //pushing the sum to the stack and adding to the counter
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


//add sum = sum + counter
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


//pop local 0 Guardamos o resultado da soma no local
@LCL
D=M
@0	
D=D+A
@R13
M=D
@SP
M=M-1
A=M
D=M
@R13
A=M
M=D


//push argument 0 pushamos o counter
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

//push constant 1 pushamos 1
@1
D=A
@SP
A=M
M=D
@SP
M=M+1


//sub counter = counter - 1
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

//pop argument 0 guardamos o counter no argument
@ARG
D=M
@0
D=D+A
@R13
M=D
@SP
M=M-1
A=M
D=M
@R13
A=M
M=D


//push argument 0 Pushamos o counter para o stack
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

//Vemos se *SP é T/F se T vamos para o loop, se false saimos do loop
//Assumimos que 0 é false e todos os restantes valores são true!!
//Sendo assim verificamos a condição se *SP != 0, se sim é true, se não é false

//Partindo o problema em partes pequenas fazemos primeiro a condição e só depois o salto

//Por lógica boolena *SP != 0 é igual a !(*SP == 0)
//Verificamos primeiro se *SP == 0

//Então pushamos 0 para o stack
@0
D=A
@SP
A=M
M=D
@SP
M=M+1

//Verificamos se *SP != 0
@SP
M=M-1
A=M
D=M
@SP
M=M-1
A=M
D=M-D
@LOOP_START.1 //se *SP != 0 vamos para este label
D;JNE
@SP
A=M
M=0
@CONTINUE.1
0;JMP
(CONTINUE.1)


//if-goto LOOP_START  // If counter != 0, goto LOOP_START



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
