// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
//
// This program only needs to handle arguments that satisfy
// R0 >= 0, R1 >= 0, and R0*R1 < 32768.

//Pseudocode
//   let sum = 0;
//   for(let i = 0; i < multiplicador; i++)
//       sum = sum + multiplicando;  
//   return sum;

@R2
M = 0

@i
M = 0

@R0
D=M
@END
D, JLE

@R1
D=M
@END
D, JLE

@R1
D=M

@32767
D = A
@maxValue
M = D

(LOOP)
@R2
D = M
@maxValue
D = M - D
@END
D, JLE

@R0
D=M
@R2
M = D + M


@i
M = M + 1
D = M
@R1
D = M - D
@LOOP
D,JNE

(END)
@END
0;JMP



