create a small wrapper around apache.commons.compress that can take a mapping between compressed files


ex.


bigfile.zip
  |
  | smallfile.zip
      |
      |--folder/innerfile.txt


transform to


newname.zip
    |
    |--innerfile.txt


with some kind of format like

[[bigfile.zip]]/[[smallfile.zip]]/folder/innerfile.txt --> [[newname.zip]]/innerfile.txt

---------------------------------------------------------




.
├── res1.tar
│   ├── pictures
│   │   ├── b2.jpg
│   │   └── b.jpg
│   ├── aa.txt
│   ├── b
│   │   └── bb.txt
│   ├── bb.txt
│   ├── blob.scala
└── scala.tar
    ├── ArTest.scala
    └── Bzip2Test.scala


new structure

Pictures.zip
├── b2.jpg
└── b.jpg

rest1.zip
├── aa.txt
├── b
│   └── bb.txt
└── bb.txt

scala.zip
├── blob.scala
├── ArTest.scala
└── Bzip2Test.scala


New Archives can only contain 1..N Files from OrigArchive, WHERE len(OrigArchive[files]) == N
No duplicate mappings
Ver 1. Archives can only be moved same level or closer to root.

create all rootarchives.
Iterate outer
foreach subarchive mv file or mv subfiles into either Root archives or 
  ver 1. open Archive 
  ver 2. open archives, where the open Archives cannot be in a root archive which also contains the open archive file.

ArStream = (ArchiveEntry, InputStream)

for ar in outer:
   mapping[ar] match {
      MoveAll(nar) => repack(ar, nar) <- resourcemngt
      MoveSom(nars: Seq[Ar], files: Seq[ArStream])

   }
