import os

def getFilesName():
	files = list()
	directory = os.fsencode("./")
	for f in os.listdir(directory):
		filename = os.fsdecode(f)
		if filename.endswith(".txt") and filename != "log_processing_result.txt":
			files.append(filename)
	return files

def main():
	result_file = open("log_processing_result.txt", "w+")
	files = getFilesName()
	for fn in files:
		log = open(fn, "r")
		lines = log.readlines()
		ts = 0
		ts_count = 0
		tj = 0
		tj_count = 0
		for l in lines:
			if l.startswith('TS'):
				ts += int(l[4:])
				ts_count += 1
			elif l.startswith('TJ'):
				tj += int(l[4:])
				tj_count += 1
			else:
				print(fn+" :unrecognized line: "+l)
		ts_avg = ts/float(ts_count)
		tj_avg = tj/float(ts_count)

		result_file.write(fn+":\n")
		result_file.write("TS Avg: "+str(ts_avg)+"\n")
		result_file.write("TJ Avg: "+str(tj_avg)+"\n")
		
		log.close()

	result_file.close()

if __name__ == '__main__':
	main()
