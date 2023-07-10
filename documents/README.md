# Interesting links

https://mdfs.net/ - archive of BBC micro software
https://tobylobster.github.io/mos/mos/S-s1.html - annotated description of the MOS(1.2)

# Interrupts
Video Introduction to Interrupts on the BBC Micro
http://abug.org.uk/index.php/2020/08/20/bbc-micro-interrupts-part-1/

New Advanced User Guide pp. 124

http://www.6502.org/tutorials/interrupts.html

Interrupts are _external_ to the 6502

Other parts of the aystem raise these to the 6502 using one of the pins on the 6502

Two Types (each on 1 pin):
* Non-Maskable Interrupt (NMI)
* Maskable Interrupt (IRQ = Interrupt Request)

NMIs are used for disc data transfer etc.

IRQs can be marked (ignored) by using the SEI instruction (Set interrupt disable flag)
The thing that raised the interrupt still happens even when the CPU has masked it.

## What happens when an IRQ is received?

CPU state pushed to stack, JMP to location stored in 0xFFFE/F

## What BBC hardware can generate IRQs
* IRQ - RS423 serial port (cassette) character received
* ??? - 6845 CRTC (video) chip - vertical sync pulse
* IRQ - Hardware timers - a timer reached 0
* ??? - ADC conversion completed on a channel ?
* IRQ - Keyboard - keypressed
* printer port - ready to accept new character
* IRQ - peripheral connected to 1MHz bus
* li ht pen !!

## What does MOS interrupt handler do?

Since it's in ROM this is fixed to start with.
* Check whether the IRQ was caused by a BRK (software interrupt)
* Indirect jump through to IRQ1V - JMP (&204)
  * Main body of the MOS routine to server interrupts (&DC93)
* Indirect jump through to IRQ2V - JMP (&206)
  * This calls RTI

## What order does MOS service interrupts:

1. 6850 serial chip (RS423 or cassette)
2. Vsync pulse, triggers events
3. Centisecond (100Hz) timer reaches 0 - update TIME, process 10ms of SOUND, key repeats
4. ADC conversion (Analogue digital convesion)
5. Key pressed - mark key value as current key (processed in timer)
6. Printer port - send next character when ready

Interrupts are re-entrant, another could fire while you're processing an interrupt, which will then get processed.

## VIAs to the rescue:
* Most hardware devices connected to the 6502 through a 6522 Versatile Interface Adapter (VIA) chip
* A 6522 VIA also contains 2x programmable timers running at 1Mhz
* BBC has 2x VIA chips - System VIA and User VIA
* System VIA connected to: Speech, sound, keyboard, 6845 CRTC, ADC, CMOS
* User VIA connected to user port, printer port
* For games/demo we're mostly interested in vsync and timer IRQs
