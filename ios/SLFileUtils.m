

#import <Foundation/Foundation.h>
#import "SLFileUtils.h"

@implementation SLFileUtils


+ (NSFileManager*)getFileManager
{
    return [NSFileManager defaultManager];
}

+ (NSString*)readJsonDataString:(NSString*)srcData key:(NSString*)key
{
  if (srcData == nil) return nil;
  
  NSDictionary *pJson = [NSJSONSerialization JSONObjectWithData:[srcData dataUsingEncoding:NSUTF8StringEncoding] options:kNilOptions error:nil];
  if (pJson == nil) return nil;
  
  return [pJson objectForKey:key];
}

+ (NSString*)readJsonFileString:(NSString*)path key:(NSString*)key
{
  NSFileManager *fileManager = [NSFileManager defaultManager];
  if (fileManager == nil) {
    return nil;
  }
  if (![fileManager fileExistsAtPath:path]) {
    return nil;
  }
  
  NSString *srcData = [[NSString alloc] initWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
  return [SLFileUtils readJsonDataString:srcData key:key];
}

+ (NSDictionary*)readJsonToDictionary:(NSString*)path
{
  NSFileManager *fileManager = [NSFileManager defaultManager];
  if (fileManager == nil) {
    return nil;
  }
  if (![fileManager fileExistsAtPath:path]) {
    return nil;
  }
  
  NSString *srcData = [[NSString alloc] initWithContentsOfFile:path encoding:NSUTF8StringEncoding error:nil];
  if (srcData == nil) return nil;
  
  return [NSJSONSerialization JSONObjectWithData:[srcData dataUsingEncoding:NSUTF8StringEncoding] options:kNilOptions error:nil];
}

+ (BOOL)saveJson:(NSString*)path dic:(NSDictionary*)dic
{
  if (path == nil) {
    return FALSE;
  }
  if (dic == nil) {
    return FALSE;
  }
  
  NSData *data = [NSJSONSerialization dataWithJSONObject:dic
                                                 options:NSJSONWritingPrettyPrinted
                                                   error:nil
                  ];
  
  if (data == nil) {
    return FALSE;
  }
  
  return [data writeToFile:path atomically:YES];
}

+ (NSString*)readBundleFile:(NSString*)path {
    NSString *mainBundleDirectory=[[NSBundle mainBundle] bundlePath];
    
    NSString *path1 = [mainBundleDirectory stringByAppendingPathComponent:path];
    
    NSURL *url = [NSURL fileURLWithPath:path1];
    
    return [SLFileUtils readFileString:url encoding:NSUTF8StringEncoding];
}

+ (NSString*)readFileUtf8String:(NSString*)path {
    NSURL *url = [NSURL fileURLWithPath:path];
    return [SLFileUtils readFileString:url encoding:NSUTF8StringEncoding];
}

+ (NSString*)readFileString:(NSURL*)url encoding:(NSStringEncoding)encoding {
    NSData *data = [[NSData alloc] initWithContentsOfURL:url];
    if (!data) {
        return nil;
    }
    
    return [[NSString alloc] initWithData:data encoding:encoding];
}

+ (NSString*)getDocumentDirectory
{
    return [SLFileUtils getPathForDirectory:NSDocumentDirectory];
}

+ (NSString*)getCachesDirectory
{
    return [SLFileUtils getPathForDirectory:NSCachesDirectory];
}

+ (BOOL)exists:(NSString*)path
{
    return [SLFileUtils exists:[NSFileManager defaultManager] path:path];
}
+ (BOOL)exists:(NSFileManager*)fileManager path:(NSString*)path
{
    if (fileManager == nil) {
        return FALSE;
    }
    return [fileManager fileExistsAtPath:path];
}

+ (BOOL)copyFile:(NSString*)src dst:(NSString*)dst
{
    return [SLFileUtils copyFile:[NSFileManager defaultManager] src:src dst:dst];
}
+ (BOOL)copyFile:(NSFileManager*)fileManager src:(NSString*)src dst:(NSString*)dst
{
    if (fileManager == nil) {
        return FALSE;
    }
    
    if (![SLFileUtils makeFileFolder:fileManager path:dst]) {
        return FALSE;
    }
    
    //cover copy
    [SLFileUtils deleteFile:fileManager path:dst];
    
    NSError *errorInfo;
    BOOL r = [fileManager copyItemAtPath:src toPath:dst error:&errorInfo];
    if (!r) {
        NSLog(@"===>>Copy file error : %@", errorInfo);
    }
//    else {
//        NSLog(@"===>>Copy from:%@ to:%@ success", src, dst);
//    }
    return r;
}

+ (BOOL)moveFile:(NSString*)src dst:(NSString*)dst
{
    return [SLFileUtils moveFile:[NSFileManager defaultManager] src:src dst:dst];
}
+ (BOOL)moveFile:(NSFileManager*)fileManager src:(NSString*)src dst:(NSString*)dst
{
    if (fileManager == nil) {
        return FALSE;
    }
    
    if (![SLFileUtils makeFileFolder:fileManager path:dst]) {
        return FALSE;
    }
    
    //cover copy
    [SLFileUtils deleteFile:fileManager path:dst];
    
    NSError *errorInfo;
    BOOL r = [fileManager copyItemAtPath:src toPath:dst error:&errorInfo];
    if (!r) {
        NSLog(@"===>>Copy file error : %@", errorInfo);
        return FALSE;
    }
    
    r = [fileManager removeItemAtPath:src error:&errorInfo];
    if (!r) {
        NSLog(@"===>>delete file error : %@", errorInfo);
    }
//    else {
//        NSLog(@"===>>move from:%@ to:%@ success", src, dst);
//    }
    return r;
}

+ (BOOL)moveFolder:(NSString*)src dst:(NSString*)dst
{
    return [SLFileUtils moveFolder:[NSFileManager defaultManager] src:src dst:dst];
}

+ (BOOL)moveFolder:(NSFileManager*)fileManager src:(NSString*)src dst:(NSString*)dst
{
    if (fileManager == nil) {
        return FALSE;
    }
    
    BOOL isDir;
    [fileManager fileExistsAtPath:src isDirectory:&isDir];
    if (!isDir) {
        return [SLFileUtils moveFile:fileManager src:src dst:dst];
    }
    
    NSDirectoryEnumerator *fileEnumerator = [fileManager enumeratorAtPath:src];
    for (NSString *fileName in fileEnumerator) {
        NSString *srcFilePath = [src stringByAppendingPathComponent:fileName];
        NSString *dstFilePath = [dst stringByAppendingPathComponent:fileName];
        BOOL r = [SLFileUtils moveFolder:fileManager src:srcFilePath dst:dstFilePath];
        if (!r) return FALSE;
    }
    
    
    return TRUE;
}

+ (BOOL)deleteFile:(NSString*)path
{
    return [SLFileUtils deleteFile:[NSFileManager defaultManager] path:path];
}
+ (BOOL)deleteFile:(NSFileManager*)fileManager path:(NSString*)path
{
    if (fileManager == nil) {
        return FALSE;
    }
    if (![fileManager fileExistsAtPath:path]) {
        return TRUE;
    }
    
    NSError *errorInfo;
    BOOL r = [fileManager removeItemAtPath:path error:&errorInfo];
    if (!r) {
        NSLog(@"===>>delete file error : %@", errorInfo);
    }
//    else {
//        NSLog(@"===>>delete file:%@ success", path);
//    }
    return r;
}

+ (BOOL)makeFileFolder:(NSString*)path {
    return [SLFileUtils makeFileFolder:[NSFileManager defaultManager] path:path];
}

+ (BOOL)makeFileFolder:(NSFileManager*)fileManager path:(NSString*)path {
    NSString* dstFolder = [path stringByDeletingLastPathComponent];
    return [SLFileUtils mkdirs:fileManager folderPath:dstFolder];
}

+ (BOOL)mkdirs:(NSString*)folderPath {
    return [SLFileUtils mkdirs:[NSFileManager defaultManager] folderPath:folderPath];
}

+ (BOOL)mkdirs:(NSFileManager*)fileManager folderPath:(NSString*)folderPath {
    if (![fileManager fileExistsAtPath:folderPath]) {
        NSError *errorInfo;
        BOOL r = [fileManager createDirectoryAtPath:folderPath withIntermediateDirectories:YES attributes:nil error:&errorInfo];
        if (!r) {
            NSLog(@"===>>mkdirs error : %@", errorInfo);
            return FALSE;
        }
    }
    
    return TRUE;
}

+ (NSArray<NSString*>*)listFiles:(NSString*)folderPath {
    return [SLFileUtils listFiles:[NSFileManager defaultManager] folderPath:folderPath];
}

+ (NSArray<NSString*>*)listFiles:(NSFileManager*)fileManager folderPath:(NSString*)folderPath {
    NSMutableArray<NSString*>* ret = [NSMutableArray new];
    NSDirectoryEnumerator *fileEnumerator = [fileManager enumeratorAtPath:folderPath];
    for (NSString *fileName in fileEnumerator) {
        NSString *filePath = [folderPath stringByAppendingPathComponent:fileName];
        [ret addObject:filePath];
    }
    return ret;
}

+ (NSString *)getPathForDirectory:(NSSearchPathDirectory)directory
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(directory, NSUserDomainMask, YES);
    if (paths == nil) {
        return nil;
    }
    if ([paths count] <= 0) {
        return nil;
    }
  return [paths firstObject];
}

@end
