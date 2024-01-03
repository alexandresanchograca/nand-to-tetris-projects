//O tradutor deve iniciar o programa com function Sys.init
//Esta funcao chama a funcção Main.main e entra num loop infinito
//Serve para inicializar o programa
//SP = 256
//call Sys.init

//SP = 256
@256
D=A
@SP
M=D

//call Sys.init

//call Sys.main 0
//Definimos o endereço de retorno gerando uma label
//Pushamos o endereço da label para o stack
@functionName$RETURN_LABEL
D=A
@SP 
A=M 
M=D 
@SP 
M=M+1

//Guardamos os valores dos ponteiros dos segmentos de memória 
//*SP[3] = memSegmentBasePtr
@LCL
D=M
@SP
A=M
M=D
@SP
M=M+1

@ARG
D=M
@SP
A=M
M=D
@SP
M=M+1

@THIS
D=M
@SP
A=M
M=D
@SP
M=M+1

@THAT
D=M
@SP
A=M
M=D
@SP
M=M+1

//Reposicionamos o ptr de ARG para apontar para o valor dos argumentos passados
//Sabemos que foram pushados para os stack 4 ponteiros de segmentos de memória
//1 ponteiro do endereço de retorno da função e um numero de argumentos
//sendo assim reposicionamos o ARG em SP - (4+1) - nArgs -> ARG = SP - 5 - nArgs
//ARG = SP - 5 - nArgs
@5 //nArgs
D=A
@5 //number of basePtrs saved to the stack
D=A+D //numBasePtrs + numArgs
@SP
D=M-D
@ARG
M=D

//Setting the LCL ptr
//LCL = SP 
@SP
D=M
@LCL
M=D

//goto called function
@Sys.main
0;JMP

//Function return-adress label to resume the executing here
(functionName$RETURN_LABEL)








