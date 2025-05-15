

#ifndef FileUtils_h
#define FileUtils_h

@interface FileUtils : NSObject
{
  
}

+ (NSFileManager*)getFileManager;

+ (NSString*)readJsonDataString:(NSString*)srcData key:(NSString*)key;

+ (NSString*)readJsonFileString:(NSString*)path key:(NSString*)key;

+ (NSDictionary*)readJsonToDictionary:(NSString*)path;

+ (BOOL)saveJson:(NSString*)path dic:(NSDictionary*)dic;

+ (NSString*)readBundleFile:(NSString*)path;
+ (NSString*)readFileUtf8String:(NSString*)path;

+ (NSString*)getDocumentDirectory;

+ (NSString*)getCachesDirectory;

+ (BOOL)exists:(NSString*)path;
+ (BOOL)exists:(NSFileManager*)fileManager path:(NSString*)path;

+ (BOOL)copyFile:(NSString*)src dst:(NSString*)dst;
+ (BOOL)copyFile:(NSFileManager*)fileManager src:(NSString*)src dst:(NSString*)dst;

+ (BOOL)moveFile:(NSString*)src dst:(NSString*)dst;
+ (BOOL)moveFile:(NSFileManager*)fileManager src:(NSString*)src dst:(NSString*)dst;
+ (BOOL)moveFolder:(NSString*)src dst:(NSString*)dst;
+ (BOOL)moveFolder:(NSFileManager*)fileManager src:(NSString*)src dst:(NSString*)dst;

+ (BOOL)deleteFile:(NSString*)path;
+ (BOOL)deleteFile:(NSFileManager*)fileManager path:(NSString*)path;

+ (BOOL)mkdirs:(NSString*)folderPath;
+ (BOOL)mkdirs:(NSFileManager*)fileManager folderPath:(NSString*)folderPath;

+ (BOOL)makeFileFolder:(NSString*)path;
+ (BOOL)makeFileFolder:(NSFileManager*)fileManager path:(NSString*)path;

+ (NSArray<NSString*>*)listFiles:(NSString*)folderPath;
+ (NSArray<NSString*>*)listFiles:(NSFileManager*)fileManager folderPath:(NSString*)folderPath;

+ (NSString *)getPathForDirectory:(NSSearchPathDirectory)directory;

@end



#endif /* FileUtils_h */
