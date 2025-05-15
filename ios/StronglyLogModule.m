

#import "StronglyLogModule.h"
#import <SLog.h>
#import <SLFileUtils.h>
#import <SSZipArchive.h>

@implementation StronglyLogModule

RCT_EXPORT_MODULE(SLog);


+ (BOOL)requiresMainQueueSetup
{
  return YES;
}

- (dispatch_queue_t)methodQueue
{
  return dispatch_get_main_queue();
}

#pragma mark - log

RCT_REMAP_METHOD(debug, message:(NSString*)message) {
  [SLog d:message];
}

RCT_REMAP_METHOD(info, message2:(NSString*)message) {
  [SLog i:message];
}

RCT_REMAP_METHOD(warn, message3:(NSString*)message) {
  [SLog w:message];
}

RCT_REMAP_METHOD(error, message4:(NSString*)message) {
  [SLog e:message];
}

#pragma mark - clearOldLogs

RCT_REMAP_METHOD(clearOldLogs, resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
  dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
    [SLog clearOldLogs];
    
    resolve(@(1));
  });
}

#pragma mark - zipLogFiles

RCT_REMAP_METHOD(zipLogFiles, resolve1:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
  dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
    NSString* logFileFolder = [SLog getLogFileFolder];
    NSString* logZipFolder = [SLog getLogZipFolder];
    NSString* zipFile = [logZipFolder stringByAppendingPathComponent:[NSString stringWithFormat:@"%llu", (UInt64)[[NSDate date] timeIntervalSince1970] * 1000]];
    
    NSString* ret = nil;
    
    [SLFileUtils mkdirs:logZipFolder];
    
    if ([SSZipArchive createZipFileAtPath:zipFile withContentsOfDirectory:logFileFolder]) {
      ret = zipFile;
    }
    
    resolve(ret);
  });
}

#pragma mark - clearAllLogs

RCT_REMAP_METHOD(clearAllLogs, resolve2:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
  dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
    NSString* logFileFolder = [SLog getLogFileFolder];
    NSString* nowLogFile = [SLog getLogFile];
    
    NSFileManager* fm = [NSFileManager defaultManager];
    
    NSArray<NSString*>* files = [SLFileUtils listFiles:fm folderPath:logFileFolder];
    for (NSString* file in files) {
      BOOL isDirectory;
      [fm fileExistsAtPath:file isDirectory:&isDirectory];
      if (isDirectory) continue;
      
      //不删除当前使用的日志
      if ([file isEqual:nowLogFile]) {
        continue;
      }
      
      [SLFileUtils deleteFile:fm path:file];
    }
    
    resolve(@(1));
  });
}

#pragma mark - flush

RCT_REMAP_METHOD(flush, resolve2:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject) {
  dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
    [SLog flushLogFile];
    
    resolve(@(1));
  });
}

@end
