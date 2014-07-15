create tablespace ${instance.config.tablespace} 
datafile '${instance.config.tablespace}.dat' 
size 10M 
autoextend on 
maxsize ${plan.metadata.other.max_size} 
extent management local 
uniform size 64K;

create temporary tablespace ${instance.config.tablespace}_temp 
tempfile '${instance.config.tablespace}_temp.dat'
size 10M 
autoextend on next 32m 
maxsize ${plan.metadata.other.max_size}
extent management local