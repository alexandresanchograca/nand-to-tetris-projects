

addr = SCREEN; // 1682~
i = 0;

for(let i = 0; i < 8191; i++){
    RAM[addr] = -1;
    addr++;
}