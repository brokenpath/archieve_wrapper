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

[[bigfile.zip]]/[[smallfile]]/folder/innerfile.txt --> [[newname.zip]]/innerfile.txt